package biz.pub.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 퍼블리싱에 사용하기 위한 컨트롤
 */
@Controller
@RequestMapping(value = "/pub")
public class PublishController {

    @GetMapping(value = "/callPopup.do")
    public String callPopup() {
        return "pub/callPopup";
    }

    // components
    @GetMapping(value = "/components.do")
    public String components() {
        return "pub/temp/components";
    }

    @GetMapping(value = "/template1.do")
    public String template1() {
        return "pub/temp/template1";
    }

    /*
     * 팝업 영역
     * 1. [기능명]  /pub/popup/template1.do?index=1
     * */
    @GetMapping(value = "/popup/template.do")
    public String popupTemplate1(@RequestParam(name = "index") int index) {
        return "pub/popup/template" + index;
    }
}
