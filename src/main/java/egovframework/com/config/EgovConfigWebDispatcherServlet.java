package egovframework.com.config;

import egovframework.com.cmm.interceptor.CustomAuthenticInterceptor;
import egovframework.com.cmm.interceptor.WebLogInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName : EgovConfigWebDispatcherServlet.java
 * @Description : DispatcherServlet 설정
 *
 * @author : 윤주호
 * @since  : 2021. 7. 20
 * @version : 1.0
 */
@Configuration
@ComponentScan(basePackages = { "egovframework", "biz" }, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Service.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
})
public class EgovConfigWebDispatcherServlet implements WebMvcConfigurer {

    private final WebLogInterceptor webLogInterceptor;

    public EgovConfigWebDispatcherServlet(WebLogInterceptor webLogInterceptor) {
        this.webLogInterceptor = webLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomAuthenticInterceptor())
                .addPathPatterns("/**/*.do")
                .excludePathPatterns("/login/**", "/auth/**", "/error/**");

        registry.addInterceptor(webLogInterceptor)
                .addPathPatterns("/**/*.do", "/api/**")
                .excludePathPatterns("/login/**", "/auth/**", "/error/**", "/system/wlg/**", "/api/system/wlg/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/login/loginForm.do");
        registry.addViewController("/cmmn/validator.do").setViewName("cmmn/validator");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/", "classpath:/public/js/", "/js/");

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/public/css/", "/css/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/", "classpath:/public/images/", "/images/");
    }
}
