package biz.system.wlg.service.impl;

import biz.system.wlg.dao.WebLogDAO;
import biz.system.wlg.service.WebLogService;
import biz.system.wlg.vo.WebLogVO;
import egovframework.com.cmm.response.PagingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 웹로그 Service 구현체
 */
@Slf4j
@Service("webLogService")
public class WebLogServiceImpl implements WebLogService {

    @Resource(name = "webLogDAO")
    private WebLogDAO webLogDAO;

    @Override
    public void createWebLog(WebLogVO webLogVO) {
        try {
            webLogDAO.insertWebLog(webLogVO);
        } catch (Exception e) {
            log.error("웹로그 저장 중 오류 발생", e);
        }
    }

    @Override
    public Map<String, Object> getWebLogList(WebLogVO webLogVO) {
        int pageNo = webLogVO.getSearchVO() != null ? webLogVO.getSearchVO().getPageNo() : 1;
        int pageSize = 10;

        if (pageNo <= 0) {
            pageNo = 1;
        }

        webLogVO.getSearchVO().setPageNo(pageNo);
        webLogVO.getSearchVO().setPageSize(pageSize);
        webLogVO.getSearchVO().setPageUnit(pageSize);
        webLogVO.getSearchVO().calculateOffset();

        int totalCount = webLogDAO.selectWebLogListCount(webLogVO);
        List<WebLogVO> list = webLogDAO.selectWebLogList(webLogVO);
        PagingVO pagingVO = new PagingVO(totalCount, pageNo, pageSize);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", list);
        resultMap.put("pagingVO", pagingVO);
        return resultMap;
    }
}
