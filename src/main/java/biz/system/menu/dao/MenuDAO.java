package biz.system.menu.dao;

import biz.system.menu.vo.MenuAuthVO;
import biz.system.menu.vo.MenuVO;
import biz.system.menu.vo.UserMenuAuthVO;
import biz.system.menu.vo.UserRoleVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 메뉴 관리를 위한 DAO 클래스
 */
@Repository("menuDAO")
public class MenuDAO extends EgovAbstractMapper {

    /**
     * 권한별 메뉴 목록 조회
     * @param roleCd 권한 코드
     * @return 메뉴 목록
     */
    public List<MenuVO> selectMenuListByRole(String roleCd) {
        return selectList("menuDAO.selectMenuListByRole", roleCd);
    }

    /**
     * 사용자별 메뉴 목록 조회
     * @param userId 사용자ID
     * @return 메뉴 목록
     */
    public List<MenuVO> selectMenuListByUser(String userId) {
        return selectList("menuDAO.selectMenuListByUser", userId);
    }

    /**
     * 권한 그룹 및 사용자별 메뉴 목록 조회 (통합)
     * @param roleCd 권한 코드
     * @param userId 사용자ID
     * @return 메뉴 목록
     */
    public List<MenuVO> selectMenuListByRoleAndUser(String roleCd, String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("roleCd", roleCd);
        params.put("userId", userId);
        return selectList("menuDAO.selectMenuListByRoleAndUser", params);
    }

    /**
     * 관리자용 전체 메뉴 목록 조회
     * @param menuVO 검색 조건
     * @return 메뉴 목록
     */
    public List<MenuVO> selectMenuList(MenuVO menuVO) {
        return selectList("menuDAO.selectMenuList", menuVO);
    }

    /**
     * 메뉴 상세 조회
     * @param menuId 메뉴ID
     * @return 메뉴 상세 정보
     */
    public MenuVO selectMenuDetail(String menuId) {
        return selectOne("menuDAO.selectMenuDetail", menuId);
    }

    /**
     * 메뉴 등록
     * @param menuVO 메뉴 정보
     * @return 등록 결과
     */
    public int insertMenu(MenuVO menuVO) {
        return insert("menuDAO.insertMenu", menuVO);
    }

    /**
     * 메뉴 수정
     * @param menuVO 메뉴 정보
     * @return 수정 결과
     */
    public int updateMenu(MenuVO menuVO) {
        return update("menuDAO.updateMenu", menuVO);
    }

    /**
     * 메뉴 삭제
     * @param menuId 메뉴ID
     * @return 삭제 결과
     */
    public int deleteMenu(String menuId) {
        return delete("menuDAO.deleteMenu", menuId);
    }

    /**
     * 특정 메뉴에 대한 모든 권한 그룹 목록 조회
     * @param menuId 메뉴ID
     * @return 권한 그룹 목록
     */
    public List<MenuAuthVO> selectRoleListByMenu(String menuId) {
        return selectList("menuDAO.selectRoleListByMenu", menuId);
    }

    /**
     * 특정 권한 그룹에 속한 사용자 목록 조회
     * @param roleCd 권한 코드
     * @return 사용자 목록
     */
    public List<UserRoleVO> selectUserListByRole(String roleCd) {
        return selectList("menuDAO.selectUserListByRole", roleCd);
    }

    /**
     * 메뉴-권한 매핑 저장 (UPSERT)
     * @param menuAuthVO 권한 매핑 정보
     * @return 저장 결과
     */
    public int insertOrUpdateMenuAuth(MenuAuthVO menuAuthVO) {
        return insert("menuDAO.insertOrUpdateMenuAuth", menuAuthVO);
    }

    /**
     * 특정 메뉴에 대한 모든 권한 매핑 삭제
     * @param menuId 메뉴ID
     * @return 삭제 결과
     */
    public int deleteAllMenuAuthByMenuId(String menuId) {
        return delete("menuDAO.deleteAllMenuAuthByMenuId", menuId);
    }

    /**
     * 특정 메뉴에 개별 권한이 부여된 사용자 목록 조회
     * @param menuId 메뉴ID
     * @return 사용자 목록
     */
    public List<UserMenuAuthVO> selectUserListByMenu(String menuId) {
        return selectList("menuDAO.selectUserListByMenu", menuId);
    }

    /**
     * 전체 사용자 목록 조회 (메뉴 권한 여부 포함)
     * @param menuId 메뉴ID
     * @return 사용자 목록
     */
    public List<UserMenuAuthVO> selectAllUsersForMenu(String menuId) {
        return selectList("menuDAO.selectAllUsersForMenu", menuId);
    }

    /**
     * 사용자-메뉴 권한 저장 (UPSERT)
     * @param userMenuAuthVO 사용자-메뉴 권한 매핑 정보
     * @return 저장 결과
     */
    public int insertOrUpdateUserMenuAuth(UserMenuAuthVO userMenuAuthVO) {
        return insert("menuDAO.insertOrUpdateUserMenuAuth", userMenuAuthVO);
    }

    /**
     * 사용자-메뉴 권한 삭제
     * @param userMenuAuthVO 사용자-메뉴 권한 매핑 정보
     * @return 삭제 결과
     */
    public int deleteUserMenuAuth(UserMenuAuthVO userMenuAuthVO) {
        return delete("menuDAO.deleteUserMenuAuth", userMenuAuthVO);
    }

    /**
     * 특정 메뉴에 대한 모든 사용자-메뉴 권한 삭제
     * @param menuId 메뉴ID
     * @return 삭제 결과
     */
    public int deleteAllUserMenuAuthByMenuId(String menuId) {
        return delete("menuDAO.deleteAllUserMenuAuthByMenuId", menuId);
    }
}
