package biz.system.user.service.impl;

import biz.system.user.dao.UserDAO;
import biz.system.user.service.UserService;
import biz.system.user.vo.UserVO;
import biz.util.EgovFileScrty;
import biz.util.SessionUtil;
import biz.util.StringUtil;
import egovframework.com.cmm.response.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 사용자 관리를 처리하는 서비스 구현 클래스
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource(name = "userDAO")
    private UserDAO userDAO;
    
    /**
     * 사용자 ID 중복 여부를 확인한다
     * @param userId 사용자 ID
     * @return 중복이면 true, 사용 가능하면 false
     */
    @Override
    public boolean checkUserIdDuplicate(String userId) throws Exception {
        // DB에서 해당 userId가 존재하는지 확인
        int count = userDAO.checkUserIdDuplicate(userId);
        // 1개 이상 존재하면 중복, 0개면 사용 가능
        return count > 0;
    }

    /**
     * 사용자 목록 전체 건수를 조회한다
     * @param userVO 검색 조건
     * @return 전체 건수
     */
    @Override
    public int getUserListCnt(UserVO userVO) throws Exception {
        return userDAO.selectUserListCnt(userVO);
    }

    /**
     * 사용자 목록을 조회한다
     * @param userVO 검색 조건 및 페이징 정보
     * @return 사용자 목록
     */
    @Override
    public List<UserVO> getUserList(UserVO userVO) throws Exception {
        return userDAO.selectUserList(userVO);
    }

    /**
     * 사용자 정보를 등록한다
     * @param userVO 사용자 정보
     * @return 처리 결과
     * @throws Exception
     */
    @Override
    public ResultVO userSave(UserVO userVO) throws Exception {
        ResultVO resultVO = new ResultVO();
        
        try {
            // 로그인 사용자 정보
            String loginUserId = SessionUtil.getUserId();
            
            // 사용자 ID 중복 체크
            boolean isDuplicate = checkUserIdDuplicate(userVO.getUserId());
            if (isDuplicate) {
                resultVO.setResultValue(false);
                resultVO.setMessage("이미 사용 중인 사용자ID입니다.");
                return resultVO;
            }
            
            // 비밀번호 암호화
            if (!StringUtil.isEmpty(userVO.getUserPassword())) {
                String encryptedPassword = EgovFileScrty.encryptPassword(userVO.getUserPassword(), userVO.getUserId());
                userVO.setUserPassword(encryptedPassword);
            }
            
            // 등록자 정보 설정
            userVO.setRegisterId(loginUserId);
            
            // 기본값 설정
            if (StringUtil.isEmpty(userVO.getUseYn())) {
                userVO.setUseYn("Y");
            }
            if (StringUtil.isEmpty(userVO.getAcntLockYn())) {
                userVO.setAcntLockYn("N");
            }
            
            int insertResult = userDAO.insertUser(userVO);
            if(insertResult > 0){
                // 권한 매핑 등록
                userDAO.insertUserRole(userVO);
                resultVO.setResultValue(true);
                resultVO.setMessage("사용자가 등록되었습니다.");
            } else {
                resultVO.setResultValue(false);
                resultVO.setMessage("사용자 등록에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("사용자 등록 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("사용자 등록 중 오류가 발생했습니다.");
        }
        
        return resultVO;
    }

    /**
     * 사용자 상세 정보를 조회한다
     * @param userId 사용자 ID
     * @return 사용자 상세 정보
     */
    @Override
    public UserVO getUserDetail(String userId) throws Exception {
        // userId 유효성 검증
        if (StringUtil.isEmpty(userId)) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        
        // 사용자 상세 정보 조회
        UserVO userVO = userDAO.selectUserDetail(userId);
        
        if (userVO == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        
        return userVO;
    }
}

