package biz.system.wlg.service;

import biz.system.wlg.vo.WebLogVO;

import java.util.Map;

/**
 * 웹로그 Service
 */
public interface WebLogService {

    void createWebLog(WebLogVO webLogVO);

    Map<String, Object> getWebLogList(WebLogVO webLogVO);
}
