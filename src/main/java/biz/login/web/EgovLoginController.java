package biz.login.web;

import biz.login.service.EgovLoginService;
import biz.login.vo.LoginVO;
import biz.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 로그인을 처리하는 컨트롤러 클래스
 * @author 공통서비스 개발팀
 * @since 2024.12.19
 * @version 1.0
 */
@Slf4j
@Controller
public class EgovLoginController {

    @Resource(name = "loginService")
    private EgovLoginService loginService;

    /**
     * 로그인 폼 페이지
     * @param error 에러 메시지 파라미터
     * @param model Model 객체
     * @return 로그인 페이지
     */
    @GetMapping("/login/loginForm.do")
    public String loginForm(Model model) {
        // 이미 로그인된 사용자는 메인 페이지로 리다이렉트
        if (SessionUtil.isAuthenticated()) {
            return "redirect:/main/mainForm.do";
        }
        
        // FlashAttribute로 전달된 오류 메시지는 자동으로 model에 추가됨
        return "login/loginForm";
    }

    /**
     * 로그인 처리
     * @param loginVO 로그인 정보
     * @param request HttpServletRequest
     * @param redirectAttributes RedirectAttributes
     * @return 리다이렉트 URL
     */
    @PostMapping("/login/actionLogin.do")
    public String actionLogin(@ModelAttribute LoginVO loginVO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            // IP 주소 추출
            String ip = getClientIp(request);
            loginVO.setIp(ip);
            
            // 세션 ID 설정
            HttpSession session = request.getSession();
            loginVO.setSessionId(session.getId());
            
            // 로그인 서비스 호출
            LoginVO resultVO = loginService.actionLogin(loginVO);

            if (resultVO != null && resultVO.getUserId() != null && !resultVO.getUserId().equals("")) {
                // 로그인 성공 - 세션에 정보 저장
                SessionUtil.setAttribute("LoginVO", resultVO);
                return "redirect:/main/mainForm.do";
            } else {
                // 로그인 실패 - Service에서 설정한 실패 사유 사용
                String errorMsg = loginVO.getLoginFailResn() != null && !loginVO.getLoginFailResn().isEmpty()
                        ? loginVO.getLoginFailResn()
                        : "아이디 또는 비밀번호가 올바르지 않습니다.";
                redirectAttributes.addFlashAttribute("loginError", errorMsg);
                return "redirect:/login/loginForm.do";
            }
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("loginError", "로그인 처리 중 오류가 발생했습니다.");
            return "redirect:/login/loginForm.do";
        }
    }

    /**
     * 로그아웃 처리
     * @param request HttpServletRequest
     * @return 리다이렉트 URL
     */
    @GetMapping("/login/logout.do")
    public String logout(HttpServletRequest request) {
        SessionUtil.removeAttribute("LoginVO");
        request.getSession().invalidate();
        return "redirect:/login/loginForm.do";
    }

    /**
     * 클라이언트 IP 주소를 가져온다.
     * @param request HttpServletRequest
     * @return IP 주소
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // X-Forwarded-For의 경우 여러 IP가 나올 수 있으므로 첫 번째 IP만 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}

