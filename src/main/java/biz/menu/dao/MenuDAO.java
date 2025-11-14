package biz.menu.dao;

import biz.menu.vo.MenuVO;
import egovframework.com.cmm.service.impl.EgovComAbstractDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 메뉴 조회를 위한 DAO 클래스
 */
@Repository("menuDAO")
public class MenuDAO extends EgovComAbstractDAO {
    
    /**
     * 권한별 메뉴 목록 조회
     * 
     * @param roleCd 권한 코드
     * @return 메뉴 목록
     */
    public List<MenuVO> selectMenuListByRole(String roleCd) {
        return selectList("menuDAO.selectMenuListByRole", roleCd);
    }
}

