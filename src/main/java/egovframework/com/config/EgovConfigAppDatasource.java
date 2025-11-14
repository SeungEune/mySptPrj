package egovframework.com.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import org.egovframe.rte.fdl.cryptography.impl.EgovARIACryptoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import java.util.Base64;

/**
 * @ClassName : EgovConfigAppDatasource.java
 * @Description : DataSource 설정
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
@Slf4j
@Configuration
public class EgovConfigAppDatasource {

	/**
	 *  Environment 의존성 주입하여 사용하는 방법
	 */
	@Autowired
	Environment env;
	
	/**
	 * 환경변수 암호화 서비스 의존성 주입
	 */
	@Autowired
	private EgovARIACryptoServiceImpl egovEnvCryptoService;

	private String dbType;

	private String className;

	private String url;

	private String userName;

	private String password;

	@PostConstruct
	void init() {
		try {
			dbType = env.getProperty("Globals.DbType");

			// 암호화된 속성값 읽기
			String encryptedClassName = env.getProperty("Globals." + dbType + ".DriverClassName");
			String encryptedUrl = env.getProperty("Globals." + dbType + ".Url");
			String encryptedUserName = env.getProperty("Globals." + dbType + ".UserName");
			String encryptedPassword = env.getProperty("Globals." + dbType + ".Password");
			
			// 복호화 수행 (Base64 디코딩 후 복호화)
			String algorithmKey = env.getProperty("Globals.crypto.algoritm");
			
			byte[] decodedClassName = Base64.getDecoder().decode(encryptedClassName);
			className = new String(egovEnvCryptoService.decrypt(decodedClassName, algorithmKey), "UTF-8");
			
			byte[] decodedUrl = Base64.getDecoder().decode(encryptedUrl);
			url = new String(egovEnvCryptoService.decrypt(decodedUrl, algorithmKey), "UTF-8");
			
			byte[] decodedUserName = Base64.getDecoder().decode(encryptedUserName);
			userName = new String(egovEnvCryptoService.decrypt(decodedUserName, algorithmKey), "UTF-8");
			
			byte[] decodedPassword = Base64.getDecoder().decode(encryptedPassword);
			password = new String(egovEnvCryptoService.decrypt(decodedPassword, algorithmKey), "UTF-8");
		} catch (Exception e) {
			log.error("DB 접속 정보 복호화 실패", e);
			throw new RuntimeException("DB 접속 정보 복호화 실패", e);
		}
	}

	/**
	 * @return [dataSource 설정] basicDataSource 설정
	 */
	private DataSource basicDataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(className);
		basicDataSource.setUrl(url);
		basicDataSource.setUsername(userName);
		basicDataSource.setPassword(password);
		return basicDataSource;
	}

	/**
	 * @return [DataSource 설정]
	 */
	@Bean(name = {"dataSource", "egov.dataSource", "egovDataSource"})
	public DataSource dataSource() {
		return basicDataSource();
	}
}
