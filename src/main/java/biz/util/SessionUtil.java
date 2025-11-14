package biz.util;

import biz.login.vo.LoginVO;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 세션 속성관리 유틸 클래스
 */
@SuppressWarnings("unchecked")
public class SessionUtil {

    /**
     * 사용자 인증여부를 체크한다.
     *
     * @return 인증된 사용자면 true, 아니면 false를 리턴한다.
     */
    public static boolean isAuthenticated() {
        LoginVO loginUser = SessionUtil.getLoginUser();
        return loginUser != null && !StringUtil.isEmpty(loginUser.getUserId());
    }

    /**
     * 사용자 관리자 여부를 체크한다.
     *
     * @return 관리자 사용자면 true, 아니면 false를 리턴한다.
     */
    public static boolean isAdminUser() {
        String roleCd = SessionUtil.getUserRoleCd();
        return roleCd != null && roleCd.equals("ADMIN");
    }
    
    /**
     * 사용자의 역할 코드를 조회한다.
     *
     * @return 사용자의 역할 코드
     */
    public static String getUserRoleCd() {
        LoginVO loginUser = SessionUtil.getLoginUser();
        return loginUser != null ? loginUser.getRoleCd() : null;
    }
    
    /**
     * name에 해당하는 세션 속성정보를 조회한다.
     *
     * @param name - 세션에서 조회할 속성정보의 name
     * @return name에 해당하는 세션 속성정보를 리턴한다.
     */
    public static Object getAttribute(String name) {
        return RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }
    
    /**
     * 입력한 name-value를 세션 속성에 설정한다.
     *
     * @param name  - 세션에서 설정할 속성정보의 name
     * @param value - 세션에서 설정할 속성정보 value
     */
    public static void setAttribute(String name, Object value) {
        RequestContextHolder.getRequestAttributes().setAttribute(name, value, RequestAttributes.SCOPE_SESSION);
    }
    
    /**
     * 입력한 name을 세션 속성에서 삭제한다.
     *
     * @param name  - 세션에서 설정한 속성정보의 name
     */
    public static void removeAttribute(String name) {
        if (SessionUtil.getAttribute(name) != null) {
            RequestContextHolder.getRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
        }
    }
    
    /**
     * 접속자의 HttpServletRequest 조회
     *
     * @return - 접속자의 HttpServletRequest 리턴
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
    
    /**
     * 접속자의 HttpServletResponse 조회
     *
     * @return - 접속자의 HttpServletResponse 리턴
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
    
    /**
     * 접속한 사용자의 정보를 조회한다.
     *
     * @return 접속한 사용자의 정보를 리턴한다.
     */
    public static LoginVO getLoginUser() {
        return (LoginVO) getAttribute("LoginVO");
    }
}
