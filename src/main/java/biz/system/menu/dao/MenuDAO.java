package biz.system.menu.dao;

import biz.system.menu.vo.MenuAuthVO;
import biz.system.menu.vo.MenuVO;
import biz.system.menu.vo.UserRoleVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 메뉴-권한 매핑 삭제
     * @param menuAuthVO 권한 매핑 정보
     * @return 삭제 결과
     */
    public int deleteMenuAuth(MenuAuthVO menuAuthVO) {
        return delete("menuDAO.deleteMenuAuth", menuAuthVO);
    }
}
