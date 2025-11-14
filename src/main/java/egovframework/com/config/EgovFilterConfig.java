package egovframework.com.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import egovframework.com.cmm.filter.HTMLTagFilter;
import egovframework.com.cmm.filter.SimpleCORSFilter;
import egovframework.com.cmm.filter.XFrameOptionsFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Boot 환경에서 사용할 커스텀 필터 설정 클래스
 * Spring Boot가 자동으로 제공하는 필터들과 충돌하지 않도록 필요한 필터만 등록
 * 
 * @author 시스템 관리자
 * @since 2025. 10. 24
 */
@Slf4j
@Configuration
public class EgovFilterConfig {

	/**
	 * CORS 필터 등록
	 * @return FilterRegistrationBean<SimpleCORSFilter>
	 */
	@Bean
	public FilterRegistrationBean<SimpleCORSFilter> corsFilter() {
		log.debug("CORS 필터 등록");
		
		FilterRegistrationBean<SimpleCORSFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SimpleCORSFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setName("SimpleCORSFilter");
		registrationBean.setOrder(1);
		
		return registrationBean;
	}

	/**
	 * HTML 태그 필터 등록 (XSS 공격 방지)
	 * @return FilterRegistrationBean<HTMLTagFilter>
	 */
	@Bean
	public FilterRegistrationBean<HTMLTagFilter> htmlTagFilter() {
		log.debug("HTML 태그 필터 등록");
		
		FilterRegistrationBean<HTMLTagFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new HTMLTagFilter());
		registrationBean.addUrlPatterns("*.do");
		registrationBean.setName("HTMLTagFilter");
		registrationBean.setOrder(2);
		
		return registrationBean;
	}

	/**
	 * X-Frame-Options 필터 등록 (클릭재킹 공격 방지)
	 * @return FilterRegistrationBean<XFrameOptionsFilter>
	 */
	@Bean
	public FilterRegistrationBean<XFrameOptionsFilter> xFrameOptionsFilter() {
		log.debug("X-Frame-Options 필터 등록");
		
		FilterRegistrationBean<XFrameOptionsFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new XFrameOptionsFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setName("XFrameOptionsFilter");
		registrationBean.setOrder(3);
		
		return registrationBean;
	}
}
