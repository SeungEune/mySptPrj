package biz.system.user.dao;

import biz.system.user.vo.UserVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사용자 관리를 처리하는 DAO 클래스
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
@Repository("userDAO")
public class UserDAO extends EgovAbstractMapper {

    /**
     * 사용자 ID 중복 여부를 확인한다
     * @param userId 사용자 ID
     * @return 중복된 사용자 ID 개수
     */
    public int checkUserIdDuplicate(String userId) {
        return selectOne("userDAO.checkUserIdDuplicate", userId);
    }
    
    /**
     * 사용자 목록 전체 건수를 조회한다
     * @param userVO 검색 조건
     * @return 전체 건수
     */
    public int selectUserListCnt(UserVO userVO) {
        return selectOne("userDAO.selectUserListCnt", userVO);
    }
    
    /**
     * 사용자 목록을 조회한다
     * @param userVO 검색 조건 및 페이징 정보
     * @return 사용자 목록
     */
    public List<UserVO> selectUserList(UserVO userVO) {
        return selectList("userDAO.selectUserList", userVO);
    }
    
    /**
     * 사용자 정보를 등록한다
     * @param userVO 사용자 정보
     * @return 등록된 행 수
     */
    public int insertUser(UserVO userVO) {
        return insert("userDAO.insertUser", userVO);
    }
    
    /**
     * 사용자 정보를 수정한다
     * @param userVO 사용자 정보
     * @return 수정된 행 수
     */
    public int updateUser(UserVO userVO) {
        return update("userDAO.updateUser", userVO);
    }

    /**
     * 사용자 권한 매핑 등록
     * @param userVO 사용자 정보 (userId, roleCd 포함)
     * @return 등록 건수
     */
    public int insertUserRole(UserVO userVO) {
        return insert("userDAO.insertUserRole", userVO);
    }

    /**
     * 사용자 권한 매핑 삭제
     * @param userId 사용자 ID
     * @return 삭제 건수
     */
    public int deleteUserRole(String userId) {
        return delete("userDAO.deleteUserRole", userId);
    }
}

