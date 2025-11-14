package egovframework.com.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import egovframework.com.cmm.interceptor.CustomAuthenticInterceptor;

/**
 * @ClassName : EgovConfigWebDispatcherServlet.java
 * @Description : DispatcherServlet 설정
 *
 * @author : 윤주호
 * @since  : 2021. 7. 20
 * @version : 1.0
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일              수정자               수정내용
 *  -------------  ------------   ---------------------
 *   2021. 7. 20    윤주호               최초 생성
 * </pre>
 *
 */
@Configuration
@ComponentScan(basePackages = { "egovframework", "biz" }, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Service.class),
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class),
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
})
public class EgovConfigWebDispatcherServlet implements WebMvcConfigurer {

	// =====================================================================
	// RequestMappingHandlerMapping 설정
	// =====================================================================
	// -------------------------------------------------------------
	// RequestMappingHandlerMapping 설정 - Interceptor 추가
	// -------------------------------------------------------------
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CustomAuthenticInterceptor()).addPathPatterns("/**/*.do").excludePathPatterns("/login/**", "/auth/**", "/error/**");
	}

	// -------------------------------------------------------------
	// RequestMappingHandlerMapping 설정 View Controller 추가
	// -------------------------------------------------------------
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/login/loginForm.do");
		registry.addViewController("/cmmn/validator.do").setViewName("cmmn/validator");
	}
	//
	//@Override
	//public void addResourceHandlers(ResourceHandlerRegistry registry) {
	//	registry.addResourceHandler("/js/**")
	//			.addResourceLocations("classpath:/static/js/", "classpath:/public/js/", "/js/");
	//
	//	registry.addResourceHandler("/css/**")
	//			.addResourceLocations("classpath:/static/css/", "classpath:/public/css/", "/css/");
	//
	//	registry.addResourceHandler("/images/**")
	//			.addResourceLocations("classpath:/static/images/", "classpath:/public/images/", "/images/");
	//}
}
