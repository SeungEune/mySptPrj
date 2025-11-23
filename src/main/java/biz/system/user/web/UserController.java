package biz.system.user.web;

import biz.util.CmmCodeUtil;
import biz.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 사용자 관리를 처리하는 컨트롤러 클래스
 * @author Spatialt 개발팀
 * @since 2025.01.20
 * @version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/system/user")
public class UserController {

    /**
     * 사용자 목록 화면
     * @param model Model 객체
     * @return 사용자 목록 페이지 또는 로그인 페이지
     */
    @GetMapping("/userListForm.do")
    public String userListForm(Model model) {
        // 세션 인증 확인
        if (!SessionUtil.isAuthenticated()) {
            return "redirect:/login/loginForm.do";
        }
        
        return "system/user/userListForm";
    }

    /**
     * 사용자 등록 화면
     * @param model Model 객체
     * @return 사용자 등록 페이지 또는 로그인 페이지
     */
    @GetMapping("/userRegisterForm.do")
    public String userRegisterForm(Model model) {
        // 세션 인증 확인
        if (!SessionUtil.isAuthenticated()) {
            return "redirect:/login/loginForm.do";
        }
        
        // 공통 코드 목록 전달
        model.addAttribute("userSttusCodeList", CmmCodeUtil.getCmmnCodeList("USER_STTUS"));
        model.addAttribute("sexdstnCodeList", CmmCodeUtil.getCmmnCodeList("SEXDSTN"));
        model.addAttribute("jbgdCodeList", CmmCodeUtil.getCmmnCodeList("JGBD"));
        model.addAttribute("jssfcCodeList", CmmCodeUtil.getCmmnCodeList("JSSFC"));
        
        return "system/user/userRegisterForm";
    }

    /**
     * 사용자 수정 화면
     * @param userId 사용자 ID
     * @param model Model 객체
     * @return 사용자 수정 페이지 또는 로그인 페이지
     */
    @GetMapping("/userForm.do")
    public String userForm(@RequestParam String userId, Model model) {
        // 세션 인증 확인
        if (!SessionUtil.isAuthenticated()) {
            return "redirect:/login/loginForm.do";
        }
        
        // userId 필수 검증
        if (userId == null || userId.isEmpty()) {
            return "redirect:/system/user/userListForm.do";
        }
        
        // 공통 코드 목록 전달
        model.addAttribute("userSttusCodeList", CmmCodeUtil.getCmmnCodeList("USER_STTUS"));
        model.addAttribute("sexdstnCodeList", CmmCodeUtil.getCmmnCodeList("SEXDSTN"));
        model.addAttribute("jbgdCodeList", CmmCodeUtil.getCmmnCodeList("JGBD"));
        model.addAttribute("jssfcCodeList", CmmCodeUtil.getCmmnCodeList("JSSFC"));
        
        model.addAttribute("userId", userId);
        
        return "system/user/userEditForm";
    }
}

