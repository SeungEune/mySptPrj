package biz.menu.service;

import biz.menu.vo.MenuVO;

import java.util.List;

/**
 * 메뉴 관리를 위한 Service 인터페이스
 */
public interface MenuService {
    
    /**
     * 권한별 메뉴 목록 조회
     * 
     * @param roleCd 권한 코드
     * @return 메뉴 목록
     */
    List<MenuVO> getMenuListByRole(String roleCd);
}

