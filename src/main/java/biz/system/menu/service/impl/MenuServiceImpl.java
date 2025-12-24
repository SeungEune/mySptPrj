package biz.system.menu.service.impl;

import biz.system.menu.dao.MenuDAO;
import biz.system.menu.service.MenuService;
import biz.system.menu.vo.MenuAuthVO;
import biz.system.menu.vo.MenuVO;
import biz.system.menu.vo.UserMenuAuthVO;
import biz.system.menu.vo.UserRoleVO;
import biz.util.SessionUtil;
import egovframework.com.cmm.response.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 메뉴 관리를 위한 Service 구현체
 */
@Slf4j
@Service("menuService")
public class MenuServiceImpl implements MenuService {
    
    @Resource(name = "menuDAO")
    private MenuDAO menuDAO;

    /**
     * 관리자용 전체 메뉴 목록 조회 (계층 구조)
     * 검색어가 있는 경우 검색된 메뉴와 상위 메뉴를 함께 반환
     * @param menuVO 검색 조건
     * @return 계층 구조로 구성된 메뉴 목록
     */
    @Override
    public List<MenuVO> getMenuList(MenuVO menuVO) {
        // 검색어가 있는 경우
        if (menuVO.getSearchKeyword() != null && !menuVO.getSearchKeyword().isEmpty()) {
            // 1. 전체 메뉴 조회 (검색 조건 없이)
            MenuVO allSearchVO = new MenuVO();
            List<MenuVO> allMenus = menuDAO.selectMenuList(allSearchVO);
            
            // 2. 검색 조건에 맞는 메뉴 ID 수집 (대소문자 구분 없이 검색)
            String keyword = menuVO.getSearchKeyword().toLowerCase();
            Set<String> matchedMenuIds = new HashSet<>();
            for (MenuVO menu : allMenus) {
                if (menu.getMenuNm() != null && 
                    menu.getMenuNm().toLowerCase().contains(keyword)) {
                    matchedMenuIds.add(menu.getMenuId());
                }
            }
            
            // 3. 검색된 메뉴의 상위 경로 메뉴 ID 수집
            Set<String> requiredMenuIds = new HashSet<>(matchedMenuIds);
            for (String menuId : matchedMenuIds) {
                collectParentMenuIds(allMenus, menuId, requiredMenuIds);
            }
            
            // 4. 필요한 메뉴만 필터링
            List<MenuVO> filteredMenus = allMenus.stream()
                .filter(m -> requiredMenuIds.contains(m.getMenuId()))
                .collect(Collectors.toList());
            
            // 5. 계층 구조로 변환
            return convertToHierarchy(filteredMenus);
        }
        
        // 검색어가 없는 경우 기존 로직
        List<MenuVO> allMenus = menuDAO.selectMenuList(menuVO);
        return convertToHierarchy(allMenus);
    }
    
    /**
     * 상위 메뉴 ID를 재귀적으로 수집
     * @param allMenus 전체 메뉴 목록
     * @param menuId 현재 메뉴 ID
     * @param result 수집된 메뉴 ID Set
     */
    private void collectParentMenuIds(List<MenuVO> allMenus, String menuId, Set<String> result) {
        for (MenuVO menu : allMenus) {
            if (menu.getMenuId().equals(menuId) && 
                menu.getUpperMenuId() != null && !menu.getUpperMenuId().isEmpty()) {
                result.add(menu.getUpperMenuId());
                collectParentMenuIds(allMenus, menu.getUpperMenuId(), result);
            }
        }
    }

    /**
     * 메뉴 상세 조회
     * @param menuId 메뉴ID
     * @return 메뉴 상세 정보
     */
    @Override
    public MenuVO getMenuDetail(String menuId) {
        return menuDAO.selectMenuDetail(menuId);
    }

    /**
     * 메뉴 저장 (등록/수정)
     * @param menuVO 메뉴 정보
     * @return 처리 결과
     */
    @Override
    public ResultVO saveMenu(MenuVO menuVO) {
        ResultVO result = new ResultVO();
        String userId = SessionUtil.getUserId();
        
        try {
            // 상위 메뉴 ID 빈 문자열 처리 (최상위 메뉴는 NULL로 설정)
            if (menuVO.getUpperMenuId() != null && menuVO.getUpperMenuId().trim().isEmpty()) {
                menuVO.setUpperMenuId(null);
            }
            
            // 메뉴 레벨 자동 계산: 상위 메뉴가 있으면 상위 메뉴 레벨 + 1, 없으면 1
            if (menuVO.getUpperMenuId() != null && !menuVO.getUpperMenuId().isEmpty()) {
                MenuVO upperMenu = menuDAO.selectMenuDetail(menuVO.getUpperMenuId());
                if (upperMenu != null && upperMenu.getMenuLevel() != null) {
                    menuVO.setMenuLevel(upperMenu.getMenuLevel() + 1);
                } else {
                    menuVO.setMenuLevel(2); // 상위 메뉴가 있지만 레벨 정보가 없는 경우
                }
            } else {
                menuVO.setMenuLevel(1); // 최상위 메뉴
            }
            
            if ("insert".equals(menuVO.getMode())) {
                // 중복 체크
                MenuVO existing = menuDAO.selectMenuDetail(menuVO.getMenuId());
                if (existing != null) {
                    result.setResultValue(false);
                    result.setMessage("이미 존재하는 메뉴ID입니다.");
                    return result;
                }
                
                menuVO.setRegisterId(userId);
                menuDAO.insertMenu(menuVO);
                result.setMessage("메뉴가 등록되었습니다.");
            } else {
                // 수정
                menuVO.setUpdusrId(userId);
                menuDAO.updateMenu(menuVO);
                result.setMessage("메뉴가 수정되었습니다.");
            }
            
            // 권한 정보 저장 (authList가 있는 경우)
            if (menuVO.getAuthList() != null && !menuVO.getAuthList().isEmpty()) {
                for (MenuAuthVO authVO : menuVO.getAuthList()) {
                    authVO.setMenuId(menuVO.getMenuId());
                    authVO.setRegisterId(userId);
                    menuDAO.insertOrUpdateMenuAuth(authVO);
                }
            }
            
            result.setResultValue(true);
        } catch (Exception e) {
            log.error("메뉴 저장 중 오류 발생", e);
            result.setResultValue(false);
            result.setMessage("저장 중 오류가 발생했습니다.");
        }
        return result;
    }

    /**
     * 메뉴 삭제
     * @param menuId 메뉴ID
     * @return 처리 결과
     */
    @Override
    public ResultVO deleteMenu(String menuId) {
        ResultVO result = new ResultVO();
        
        try {
            // 하위 메뉴 존재 여부 확인
            MenuVO searchVO = new MenuVO();
            List<MenuVO> allMenus = menuDAO.selectMenuList(searchVO);
            boolean hasChildren = allMenus.stream()
                    .anyMatch(m -> menuId.equals(m.getUpperMenuId()));
            
            if (hasChildren) {
                result.setResultValue(false);
                result.setMessage("하위 메뉴가 존재하여 삭제할 수 없습니다.");
                return result;
            }
            
            // 해당 메뉴와 연결된 모든 권한 그룹 권한 정보 삭제
            menuDAO.deleteAllMenuAuthByMenuId(menuId);
            
            // 해당 메뉴와 연결된 모든 사용자 권한 정보 삭제
            menuDAO.deleteAllUserMenuAuthByMenuId(menuId);
            
            // 메뉴 삭제
            menuDAO.deleteMenu(menuId);
            result.setResultValue(true);
            result.setMessage("메뉴가 삭제되었습니다.");
            
        } catch (Exception e) {
            log.error("메뉴 삭제 중 오류 발생", e);
            result.setResultValue(false);
            result.setMessage("삭제 중 오류가 발생했습니다.");
        }
        return result;
    }
    
    /**
     * 평면 메뉴 리스트를 계층 구조로 변환
     * 
     * @param flatMenuList 평면 메뉴 리스트
     * @return 계층 구조로 변환된 1차 메뉴 목록
     */
    private List<MenuVO> convertToHierarchy(List<MenuVO> flatMenuList) {
        if (flatMenuList == null || flatMenuList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 메뉴 ID를 키로 하는 맵 생성
        Map<String, MenuVO> menuMap = new HashMap<>();
        
        // 모든 메뉴를 맵에 저장
        for (MenuVO menu : flatMenuList) {
            menuMap.put(menu.getMenuId(), menu);
            // 하위 메뉴 리스트 초기화
            menu.setSubMenuList(new ArrayList<>());
        }
        
        // 1차 메뉴 리스트
        List<MenuVO> topMenuList = new ArrayList<>();
        
        // 계층 구조 구성
        for (MenuVO menu : flatMenuList) {
            // 1차 메뉴 (상위 메뉴 ID가 없는 경우)
            if (menu.getUpperMenuId() == null || menu.getUpperMenuId().isEmpty()) {
                topMenuList.add(menu);
            } 
            // 하위 메뉴인 경우
            else {
                MenuVO parentMenu = menuMap.get(menu.getUpperMenuId());
                if (parentMenu != null) {
                    parentMenu.getSubMenuList().add(menu);
                } else {
                    // 부모가 없는 경우, 1차 메뉴로 간주
                    topMenuList.add(menu);
                }
            }
        }
        
        return topMenuList;
    }

    /**
     * 특정 메뉴에 대한 모든 권한 그룹 목록 조회
     * @param menuId 메뉴ID
     * @return 권한 그룹 목록
     */
    @Override
    public List<MenuAuthVO> getRoleListByMenu(String menuId) {
        if (menuId == null || menuId.isEmpty()) {
            return new ArrayList<>();
        }
        return menuDAO.selectRoleListByMenu(menuId);
    }

    /**
     * 특정 권한 그룹에 속한 사용자 목록 조회
     * @param roleCd 권한 코드
     * @return 사용자 목록
     */
    @Override
    public List<UserRoleVO> getUserListByRole(String roleCd) {
        if (roleCd == null || roleCd.isEmpty()) {
            return new ArrayList<>();
        }
        return menuDAO.selectUserListByRole(roleCd);
    }

    /**
     * 메뉴-권한 매핑 저장
     * @param menuAuthVO 권한 매핑 정보
     * @return 처리 결과
     */
    @Override
    public ResultVO saveMenuAuth(MenuAuthVO menuAuthVO) {
        ResultVO result = new ResultVO();
        String userId = SessionUtil.getUserId();
        
        try {
            menuAuthVO.setRegisterId(userId);
            menuDAO.insertOrUpdateMenuAuth(menuAuthVO);
            result.setResultValue(true);
            result.setMessage("권한이 저장되었습니다.");
        } catch (Exception e) {
            log.error("권한 저장 중 오류 발생", e);
            result.setResultValue(false);
            result.setMessage("권한 저장 중 오류가 발생했습니다.");
        }
        return result;
    }

    /**
     * 특정 메뉴에 개별 권한이 부여된 사용자 목록 조회
     * @param menuId 메뉴ID
     * @return 사용자 목록
     */
    @Override
    public List<UserMenuAuthVO> getUserListByMenu(String menuId) {
        if (menuId == null || menuId.isEmpty()) {
            return new ArrayList<>();
        }
        return menuDAO.selectUserListByMenu(menuId);
    }

    /**
     * 전체 사용자 목록 조회 (메뉴 권한 여부 포함)
     * @param menuId 메뉴ID
     * @return 사용자 목록
     */
    @Override
    public List<UserMenuAuthVO> getAllUsersForMenu(String menuId) {
        if (menuId == null || menuId.isEmpty()) {
            return new ArrayList<>();
        }
        return menuDAO.selectAllUsersForMenu(menuId);
    }

    /**
     * 사용자-메뉴 권한 저장
     * @param userMenuAuthVO 사용자-메뉴 권한 매핑 정보
     * @return 처리 결과
     */
    @Override
    public ResultVO saveUserMenuAuth(UserMenuAuthVO userMenuAuthVO) {
        ResultVO result = new ResultVO();
        String userId = SessionUtil.getUserId();
        
        try {
            userMenuAuthVO.setRegisterId(userId);
            menuDAO.insertOrUpdateUserMenuAuth(userMenuAuthVO);
            result.setResultValue(true);
            result.setMessage("사용자 권한이 저장되었습니다.");
        } catch (Exception e) {
            log.error("사용자 권한 저장 중 오류 발생", e);
            result.setResultValue(false);
            result.setMessage("사용자 권한 저장 중 오류가 발생했습니다.");
        }
        return result;
    }

    /**
     * 사용자-메뉴 권한 삭제
     * @param userMenuAuthVO 사용자-메뉴 권한 매핑 정보
     * @return 처리 결과
     */
    @Override
    public ResultVO deleteUserMenuAuth(UserMenuAuthVO userMenuAuthVO) {
        ResultVO result = new ResultVO();
        
        try {
            menuDAO.deleteUserMenuAuth(userMenuAuthVO);
            result.setResultValue(true);
            result.setMessage("사용자 권한이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("사용자 권한 삭제 중 오류 발생", e);
            result.setResultValue(false);
            result.setMessage("사용자 권한 삭제 중 오류가 발생했습니다.");
        }
        return result;
    }

    /**
     * 권한 그룹 및 사용자별 메뉴 목록 조회
     * 권한 그룹 권한과 개별 사용자 권한을 UNION하여 조회
     * @param roleCd 권한 코드
     * @param userId 사용자ID
     * @return 메뉴 목록
     */
    @Override
    public List<MenuVO> getMenuListByRoleAndUser(String roleCd, String userId) {
        List<MenuVO> menuList = menuDAO.selectMenuListByRoleAndUser(roleCd, userId);
        return convertToHierarchy(menuList);
    }
}
