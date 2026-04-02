package biz.notice.web;

import biz.util.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * 공지사항 화면 Controller
 */
@Controller
@RequestMapping("/notice")
public class NoticeViewController {

    @Resource
    private AuthService authService;

    @GetMapping("/noticeListForm.do")
    public String noticeListForm(Model model) {
        model.addAttribute("canManageNotice", authService.canManageNotice());
        return "notice/noticeListForm";
    }

    @GetMapping("/noticeRegisterForm.do")
    public String noticeRegisterForm(Model model) {
        if (!authService.canManageNotice()) {
            return "redirect:/notice/noticeListForm.do";
        }
        model.addAttribute("canManageNotice", true);
        return "notice/noticeRegisterForm";
    }

    @PostMapping("/noticeEditForm.do")
    public String noticeEditForm(@RequestParam Long noticeSn, Model model) {
        if (!authService.canManageNotice()) {
            return "redirect:/notice/noticeListForm.do";
        }
        model.addAttribute("noticeSn", noticeSn);
        model.addAttribute("canManageNotice", true);
        return "notice/noticeEditForm";
    }

    @PostMapping("/noticeViewForm.do")
    public String noticeViewForm(@RequestParam Long noticeSn, Model model) {
        model.addAttribute("noticeSn", noticeSn);
        model.addAttribute("canManageNotice", authService.canManageNotice());
        return "notice/noticeViewForm";
    }
}
