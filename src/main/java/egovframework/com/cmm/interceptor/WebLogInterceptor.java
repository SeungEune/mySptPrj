package egovframework.com.cmm.interceptor;

import biz.system.wlg.service.WebLogService;
import biz.system.wlg.vo.WebLogVO;
import biz.util.SessionUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 웹로그 인터셉터
 */
@Component
public class WebLogInterceptor implements HandlerInterceptor {

    @Resource(name = "webLogService")
    private WebLogService webLogService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (!SessionUtil.isAuthenticated()) {
            return;
        }

        String requestUri = request.getRequestURI();
        if (requestUri == null || requestUri.startsWith("/css/") || requestUri.startsWith("/js/") || requestUri.startsWith("/images/")) {
            return;
        }

        WebLogVO webLogVO = new WebLogVO();
        webLogVO.setUrl(requestUri);
        webLogVO.setRqesterId(SessionUtil.getUserId());
        webLogVO.setRqesterIp(extractClientIp(request));

        webLogService.createWebLog(webLogVO);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
