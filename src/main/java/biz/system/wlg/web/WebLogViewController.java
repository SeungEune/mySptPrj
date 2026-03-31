package biz.system.wlg.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 웹로그 화면 Controller
 */
@Controller
@RequestMapping("/system/wlg")
public class WebLogViewController {

    @GetMapping("/webLogListForm.do")
    public String webLogListForm() {
        return "system/wlg/form/webLogListForm";
    }
}
