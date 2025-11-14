package biz.menu.service.impl;

import biz.menu.dao.MenuDAO;
import biz.menu.service.MenuService;
import biz.menu.vo.MenuVO;
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
@Service("MenuService")
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
                }
            }
        }
        
        return topMenuList;
    }
}

