package biz.system.menu.service.impl;

import biz.system.menu.dao.MenuDAO;
import biz.system.menu.service.MenuService;
import biz.system.menu.vo.MenuAuthVO;
import biz.system.menu.vo.MenuVO;
import biz.system.menu.vo.UserRoleVO;
import biz.util.SessionUtil;
import egovframework.com.cmm.response.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 메뉴 관리를 위한 Service 구현체
 */
@Slf4j
@Service("menuService")
public class MenuServiceImpl implements MenuService {
    
    @Resource(name = "menuDAO")
    private MenuDAO menuDAO;
    
    /**
     * 권한별 메뉴 목록 조회 (계층 구조)
     * 
     * @param roleCd 권한 코드
     * @return 계층 구조로 구성된 메뉴 목록
     */
    @Override
    public List<MenuVO> getMenuListByRole(String roleCd) {
        if (roleCd == null || roleCd.isEmpty()) {
            return List.of();  // 빈 리스트 반환
        }
        
        // 모든 레벨의 메뉴 조회
        List<MenuVO> allMenus = menuDAO.selectMenuListByRole(roleCd);
        
        // 계층 구조로 변환
        return convertToHierarchy(allMenus);
    }

    /**
     * 관리자용 전체 메뉴 목록 조회 (계층 구조)
     * @param menuVO 검색 조건
     * @return 계층 구조로 구성된 메뉴 목록
     */
    @Override
    public List<MenuVO> getMenuList(MenuVO menuVO) {
        // 모든 메뉴 조회
        List<MenuVO> allMenus = menuDAO.selectMenuList(menuVO);
        
        // 계층 구조로 변환
        return convertToHierarchy(allMenus);
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
     * 메뉴-권한 매핑 삭제
     * @param menuAuthVO 권한 매핑 정보
     * @return 처리 결과
     */
    @Override
    public ResultVO deleteMenuAuth(MenuAuthVO menuAuthVO) {
        ResultVO result = new ResultVO();
        
        try {
            menuDAO.deleteMenuAuth(menuAuthVO);
            result.setResultValue(true);
            result.setMessage("권한이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("권한 삭제 중 오류 발생", e);
            result.setResultValue(false);
            result.setMessage("권한 삭제 중 오류가 발생했습니다.");
        }
        return result;
    }
}
