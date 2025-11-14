package biz.main.web;

import biz.login.vo.LoginVO;
import biz.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인 페이지를 처리하는 컨트롤러 클래스
 * @author 공통서비스 개발팀
 * @since 2024.12.19
 * @version 1.0
 */
@Slf4j
@Controller
public class MainController {

    /**
     * 메인 페이지
     * @param model Model 객체
     * @return 메인 페이지 또는 로그인 페이지
     */
    @GetMapping("/main/mainForm.do")
    public String mainForm(Model model) {
        // SessionUtil을 사용한 로그인 정보 확인
        LoginVO loginVO = SessionUtil.getLoginUser();
        
        if (loginVO == null) {
            return "redirect:/login/loginForm.do";
        }
        
        // 로그인된 사용자 정보를 모델에 추가
        model.addAttribute("loginVO", loginVO);
        return "main/mainForm";
    }
}

