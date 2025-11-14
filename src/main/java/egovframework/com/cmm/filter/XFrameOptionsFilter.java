package egovframework.com.cmm.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.Filter;

public class XFrameOptionsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        if (uri.startsWith("/surymng/invite")) {

            String origin = req.getHeader("Origin");
            String referer = req.getHeader("Referer");
            String allowedSource = null;

            if (origin != null && !origin.isEmpty()) {
                allowedSource = origin;
            } else if (referer != null && referer.startsWith("http")) {
                try {
                    URI refUri = new URI(referer);
                    allowedSource = refUri.getScheme() + "://" + refUri.getHost()
                            + (refUri.getPort() != -1 ? ":" + refUri.getPort() : "");
                } catch (URISyntaxException e) {
                    allowedSource = null;
                }
            }

            if (allowedSource != null) {
                res.setHeader("Content-Security-Policy", "frame-ancestors 'self' " + allowedSource);
                res.setHeader("X-Frame-Options", "SAMEORIGIN");
            } else {
                res.setHeader("X-Frame-Options", "DENY");
            }

        } else {
            res.setHeader("X-Frame-Options", "DENY");
        }

        chain.doFilter(request, response);
    }
}
