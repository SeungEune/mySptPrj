package biz.system.menu.service;

import biz.system.menu.vo.MenuAuthVO;
import biz.system.menu.vo.MenuVO;
import biz.system.menu.vo.UserMenuAuthVO;
import biz.system.menu.vo.UserRoleVO;
import egovframework.com.cmm.response.ResultVO;

import java.util.List;

/**
 * 메뉴 관리를 위한 Service 인터페이스
 */
public interface MenuService {
    
    /**
     * 관리자용 전체 메뉴 목록 조회 (계층 구조)
     * @param menuVO 검색 조건
     * @return 메뉴 목록
     */
    List<MenuVO> getMenuList(MenuVO menuVO);

    /**
     * 메뉴 상세 조회
     * @param menuId 메뉴ID
     * @return 메뉴 상세 정보
     */
    MenuVO getMenuDetail(String menuId);

    /**
     * 메뉴 저장 (등록/수정)
     * @param menuVO 메뉴 정보
     * @return 처리 결과
     */
    ResultVO saveMenu(MenuVO menuVO);

    /**
     * 메뉴 삭제
     * @param menuId 메뉴ID
     * @return 처리 결과
     */
    ResultVO deleteMenu(String menuId);

    /**
     * 특정 메뉴에 대한 모든 권한 그룹 목록 조회
     * @param menuId 메뉴ID
     * @return 권한 그룹 목록
     */
    List<MenuAuthVO> getRoleListByMenu(String menuId);

    /**
     * 특정 권한 그룹에 속한 사용자 목록 조회
     * @param roleCd 권한 코드
     * @return 사용자 목록
     */
    List<UserRoleVO> getUserListByRole(String roleCd);

    /**
     * 메뉴-권한 매핑 저장
     * @param menuAuthVO 권한 매핑 정보
     * @return 처리 결과
     */
    ResultVO saveMenuAuth(MenuAuthVO menuAuthVO);

    /**
     * 특정 메뉴에 개별 권한이 부여된 사용자 목록 조회
     * @param menuId 메뉴ID
     * @return 사용자 목록
     */
    List<UserMenuAuthVO> getUserListByMenu(String menuId);

    /**
     * 전체 사용자 목록 조회 (메뉴 권한 여부 포함)
     * @param menuId 메뉴ID
     * @return 사용자 목록
     */
    List<UserMenuAuthVO> getAllUsersForMenu(String menuId);

    /**
     * 사용자-메뉴 권한 저장
     * @param userMenuAuthVO 사용자-메뉴 권한 매핑 정보
     * @return 처리 결과
     */
    ResultVO saveUserMenuAuth(UserMenuAuthVO userMenuAuthVO);

    /**
     * 사용자-메뉴 권한 삭제
     * @param userMenuAuthVO 사용자-메뉴 권한 매핑 정보
     * @return 처리 결과
     */
    ResultVO deleteUserMenuAuth(UserMenuAuthVO userMenuAuthVO);

    /**
     * 권한 그룹 및 사용자별 메뉴 목록 조회
     * @param roleCd 권한 코드
     * @param userId 사용자ID
     * @return 메뉴 목록
     */
    List<MenuVO> getMenuListByRoleAndUser(String roleCd, String userId);
}
