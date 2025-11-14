package egovframework.com.cmm.util;

import java.util.ArrayList;
import java.util.List;

import biz.util.SessionUtil;

/**
 * EgovUserDetails Helper 클래스
 *
 * @author sjyoon
 * @since 2009.06.01
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------    -------------    ----------------------
 *   2009.03.10  sjyoon    최초 생성
 *   2011.08.31  JJY            경량환경 템플릿 커스터마이징버전 생성
 *
 * </pre>
 */

public class EgovUserDetailsHelper {

	/**
	 * 인증된 사용자객체를 VO형식으로 가져온다.
	 * @return Object - 사용자 ValueObject
	 */
	public static Object getAuthenticatedUser() {
		return SessionUtil.getLoginUser();
	}

	/**
	 * 인증된 사용자의 권한 정보를 가져온다.
	 * @return List - 사용자 권한정보 목록
	 */
	public static List<String> getAuthorities() {
		// 세션 기반 인증에서는 기본 권한만 반환
		List<String> authorities = new ArrayList<>();
		if (SessionUtil.isAuthenticated()) {
			authorities.add("ROLE_USER");
			if (SessionUtil.isAdminUser()) {
				authorities.add("ROLE_ADMIN");
			}
		} else {
			authorities.add("ROLE_ANONYMOUS");
		}
		return authorities;
	}

	/**
	 * 인증된 사용자 여부를 체크한다.
	 * @return Boolean - 인증된 사용자 여부(TRUE / FALSE)
	 */
	public static Boolean isAuthenticated() {
		return SessionUtil.isAuthenticated();
	}
}
