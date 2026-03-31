package biz.system.wlg.web;

import biz.system.wlg.service.WebLogService;
import biz.system.wlg.vo.WebLogVO;
import egovframework.com.cmm.response.ApiResponseVO;
import egovframework.com.cmm.response.PagingVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 웹로그 API Controller
 */
@RestController
@RequestMapping("/system/wlg")
public class WebLogApiController {

    @Resource(name = "webLogService")
    private WebLogService webLogService;

    @PostMapping("/getList.do")
    public ResponseEntity<?> getWebLogList(@RequestBody WebLogVO webLogVO) {
        Map<String, Object> resultMap = webLogService.getWebLogList(webLogVO);
        List<WebLogVO> list = (List<WebLogVO>) resultMap.get("list");
        PagingVO pagingVO = (PagingVO) resultMap.get("pagingVO");
        return ApiResponseVO.apiResponse(list, pagingVO);
    }
}
