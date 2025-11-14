package egovframework.com.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EgovWebServletContextListener implements ServletContextListener {
	
	@Autowired
	private Environment environment;
	
	public EgovWebServletContextListener() {
		// Spring Boot 환경에서는 생성자에서 초기화하지 않음
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (System.getProperty("spring.profiles.active") == null) {
			setEgovProfileSetting();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (System.getProperty("spring.profiles.active") != null) {
			System.clearProperty("spring.profiles.active");
		}
	}

	/**
	 * Spring Boot 애플리케이션이 준비되면 실행되는 이벤트 리스너
	 * @param event ApplicationReadyEvent
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady(ApplicationReadyEvent event) {
		setEgovProfileSetting();
	}

	public void setEgovProfileSetting() {
		try {
			log.debug("===========================Start EgovServletContextLoad START ===========");
			
		// Spring Environment를 사용하여 프로파일별 프로퍼티 읽기
		String dbType = environment.getProperty("Globals.DbType", "postgres");
		
		System.setProperty("spring.profiles.active", dbType);
			log.debug("===========================END   EgovServletContextLoad END ===========");
		} catch (IllegalArgumentException e) {
			log.error("[IllegalArgumentException] Try/Catch...usingParameters Runing : " + e.getMessage());
		}
	}
}
