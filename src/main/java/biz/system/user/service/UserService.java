package biz.system.user.service;

import biz.system.user.vo.UserVO;
import egovframework.com.cmm.response.ResultVO;

import java.util.List;

/**
 * 사용자 관리를 처리하는 서비스 인터페이스
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
public interface UserService {

    /**
     * 사용자 ID 중복 여부를 확인한다
     * @param userId 사용자 ID
     * @return 중복이면 true, 사용 가능하면 false
     * @throws Exception
     */
    boolean checkUserIdDuplicate(String userId) throws Exception;

    /**
     * 사용자 목록 전체 건수를 조회한다
     * @param userVO 검색 조건
     * @return 전체 건수
     * @throws Exception
     */
    int getUserListCnt(UserVO userVO) throws Exception;

    /**
     * 사용자 목록을 조회한다
     * @param userVO 검색 조건 및 페이징 정보
     * @return 사용자 목록
     * @throws Exception
     */
    List<UserVO> getUserList(UserVO userVO) throws Exception;

    /**
     * 사용자 정보를 등록한다
     * @param userVO 사용자 정보
     * @return 처리 결과
     * @throws Exception
     */
    ResultVO userSave(UserVO userVO) throws Exception;

    /**
     * 사용자 상세 정보를 조회한다
     * @param userId 사용자 ID
     * @return 사용자 상세 정보
     * @throws Exception
     */
    UserVO getUserDetail(String userId) throws Exception;
}

