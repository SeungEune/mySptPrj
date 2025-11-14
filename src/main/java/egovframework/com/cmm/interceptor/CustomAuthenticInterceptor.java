package egovframework.com.cmm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import biz.util.SessionUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * 인증여부 체크 인터셉터 (세션 기반)
 * @author 공통서비스 개발팀 서준식
 * @since 2011.07.01
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자          수정내용
 *  -------    --------    ---------------------------
 *  2011.07.01  서준식          최초 생성
 *  2011.09.07  서준식          인증이 필요없는 URL을 패스하는 로직 추가
 *  2024.12.19  system          Spring Security 비활성화로 인한 세션 기반 활성화
 *  </pre>
 */

@Slf4j
public class CustomAuthenticInterceptor implements HandlerInterceptor {

	/**
	 * 세션에 계정정보(LoginVO)가 있는지 여부로 인증 여부를 체크한다.
	 * 계정정보(LoginVO)가 없다면, 로그인 페이지로 이동한다.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// 로그인 체크
		if (!SessionUtil.isAuthenticated()) {

			// AJAX 요청인지 확인
			String requestedWith = request.getHeader("X-Requested-With");
			if ("XMLHttpRequest".equals(requestedWith)) {
				// AJAX 요청인 경우 JSON 응답
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write("{\"error\":\"인증이 필요합니다.\",\"redirect\":\"/login/loginForm.do\"}");
				return false;
			} else {
				// 일반 요청인 경우 리다이렉트
				response.sendRedirect("/login/loginForm.do");
				return false;
			}
		}
		return true;
	}
}
