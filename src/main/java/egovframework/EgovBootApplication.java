package egovframework;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"egovframework"})
public class EgovBootApplication extends SpringBootServletInitializer {
	
	/**
	 * 외장 톰캣에서 실행될 때 사용되는 설정 메서드
	 * WAR 파일로 배포 시 외장 톰캣이 이 메서드를 호출하여 애플리케이션을 초기화
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(EgovBootApplication.class);
	}
	
	/**
	 * 내장 톰캣으로 실행될 때 사용되는 메인 메서드
	 * 개발 환경에서 직접 실행할 때 사용
	 */
	public static void main(String[] args) {
		log.debug("##### EgovBootApplication Start #####");

		SpringApplication springApplication = new SpringApplication(EgovBootApplication.class);
		springApplication.setBannerMode(Banner.Mode.OFF);
		//springApplication.setLogStartupInfo(false);
		springApplication.run(args);

		log.debug("##### EgovBootApplication End #####");
	}

}
