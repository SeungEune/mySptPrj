package biz.login.service.impl;

import biz.login.dao.LoginDAO;
import biz.login.service.EgovLoginService;
import biz.login.vo.LoginVO;
import biz.util.EgovFileScrty;
import biz.util.EgovNumberUtil;
import biz.util.EgovStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 일반 로그인을 처리하는 비즈니스 구현 클래스
 * @author 공통서비스 개발팀 박지욱
 * @since 2009.03.06
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자          수정내용
 *  -------    --------    ---------------------------
 *  2009.03.06  박지욱          최초 생성
 *  2011.08.31  JJY            경량환경 템플릿 커스터마이징버전 생성
 *  2024.12.20  시스템          신규 DB 스키마 적용
 *
 *  </pre>
 */
@Slf4j
@Service("loginService")
public class EgovLoginServiceImpl extends EgovAbstractServiceImpl implements EgovLoginService {

	@Resource(name = "loginDAO")
	private LoginDAO loginDAO;

	/**
	 * 일반 로그인을 처리한다
	 * @param vo LoginVO
	 * @return LoginVO
	 * @exception Exception
	 */
	@Override
	public LoginVO actionLogin(LoginVO vo) throws Exception {

		LoginVO loginVO = null;

		try {
			// 1. 사용자 조회
			LoginVO tempVO = new LoginVO();
			tempVO.setUserId(vo.getUserId());
			loginVO = loginDAO.actionLogin(tempVO);

			if (loginVO == null || loginVO.getUserId() == null || loginVO.getUserId().equals("")) {
				// 사용자가 존재하지 않음
				vo.setLoginFailResn("존재하지 않는 사용자");
				recordLoginHistory(vo, "N", vo.getLoginFailResn(), vo.getIp(), vo.getSessionId());
				return null;
			}

			// 2. 계정 상태 확인
			if (!"NORMAL".equals(loginVO.getUserSttusCd())) {
				vo.setLoginFailResn("계정 상태 불량 (사용 불가)");
				recordLoginHistory(vo, "N", vo.getLoginFailResn(), vo.getIp(), vo.getSessionId());
				return null;
			}

			if ("Y".equals(loginVO.getAcntLockYn())) {
				vo.setLoginFailResn("계정이 잠금 상태입니다");
				recordLoginHistory(vo, "N", vo.getLoginFailResn(), vo.getIp(), vo.getSessionId());
				return null;
			}

			// 3. 비밀번호 검증
			String enpassword = EgovFileScrty.encryptPassword(vo.getPassword(), vo.getUserId());
			
			if (!enpassword.equals(loginVO.getUserPassword())) {
				// 비밀번호 불일치 - 실패 횟수 증가
				loginDAO.updateLoginFail(loginVO);
				loginDAO.updateAccountLock(loginVO);

				// 재조회로 실패 횟수 확인
				LoginVO checkVO = loginDAO.selectUserInfo(loginVO);
				if (checkVO != null && checkVO.getLoginFailCo() != null && checkVO.getLoginFailCo() >= 5) {
					vo.setLoginFailResn("비밀번호 오류 (5회 이상 실패로 계정 잠금)");
				} else {
					vo.setLoginFailResn("비밀번호 불일치");
				}

				recordLoginHistory(vo, "N", vo.getLoginFailResn(), vo.getIp(), vo.getSessionId());
				return null;
			}

			// 4. 로그인 성공 처리
			loginDAO.updateLoginSuccess(loginVO);
			recordLoginHistory(vo, "Y", "로그인 성공", vo.getIp(), vo.getSessionId());
			return loginVO;

		} catch (Exception e) {
			vo.setLoginFailResn("시스템 오류");
			recordLoginHistory(vo, "N", vo.getLoginFailResn(), vo.getIp(), vo.getSessionId());
			return null;
		}
	}

	/**
	 * 로그인 이력을 기록한다
	 * @param vo LoginVO
	 * @param successYn 성공여부
	 * @param failReason 실패사유
	 * @param ip IP주소
	 * @param sessionId 세션ID
	 */
	private void recordLoginHistory(LoginVO vo, String successYn, String failReason, String ip, String sessionId) {
		try {
			LoginVO historyVO = new LoginVO();
			historyVO.setUserId(vo.getUserId());
			historyVO.setLoginSuccessYn(successYn);
			historyVO.setLoginFailResn(failReason);
			historyVO.setIp(ip);
			historyVO.setSessionId(sessionId);
			loginDAO.insertLoginHistory(historyVO);
		} catch (Exception e) {
			log.error("로그인 이력 기록 실패", e);
		}
	}

	/**
	 * 아이디를 찾는다.
	 * @param vo LoginVO
	 * @return LoginVO
	 * @exception Exception
	 */
	@Override
	public LoginVO searchId(LoginVO vo) throws Exception {

		// 1. 이름, 이메일주소가 DB와 일치하는 사용자 ID를 조회한다.
		LoginVO loginVO = loginDAO.searchId(vo);

		// 2. 결과를 리턴한다.
		if (loginVO != null && !loginVO.getUserId().equals("")) {
			return loginVO;
		} else {
			loginVO = new LoginVO();
		}

		return loginVO;
	}

	/**
	 * 비밀번호를 찾는다.
	 * @param vo LoginVO
	 * @return boolean
	 * @exception Exception
	 */
	@Override
	public boolean searchPassword(LoginVO vo) throws Exception {

		boolean result = true;

		// 1. 아이디, 이름, 이메일주소가 DB와 일치하는 사용자 Password를 조회한다.
		LoginVO loginVO = loginDAO.searchPassword(vo);
		if (loginVO == null || loginVO.getUserPassword() == null || loginVO.getUserPassword().equals("")) {
			return false;
		}

		// 2. 임시 비밀번호를 생성한다.(영+영+숫+영+영+숫=6자리)
		String newpassword = "";
		for (int i = 1; i <= 6; i++) {
			// 영자
			if (i % 3 != 0) {
				newpassword += EgovStringUtil.getRandomStr('a', 'z');
				// 숫자
			} else {
				newpassword += EgovNumberUtil.getRandomNum(0, 9);
			}
		}

		// 3. 임시 비밀번호를 암호화하여 DB에 저장한다.
		LoginVO pwVO = new LoginVO();
		String enpassword = EgovFileScrty.encryptPassword(newpassword, vo.getUserId());
		pwVO.setUserId(vo.getUserId());
		pwVO.setUserPassword(enpassword);
		loginDAO.updatePassword(pwVO);

		return result;
	}
}
