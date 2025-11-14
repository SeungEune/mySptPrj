package egovframework.com.cmm.service;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 응답 객체 VO
 */
@Getter
@Setter
public class ResultVO {

	// 응답 코드
	private int resultCode = 0;
	
	// 응답 메시지
	private String resultMessage = "OK";

	// 성공 메시지
	private String successMessage;

	// 오류 메시지
	private String errorMessage;

	// 전환 URL
	private String redirectUrl;

	private Map<String, Object> result = new HashMap<String, Object>();

	public void putResult(String key, Object value) {
		result.put(key, value);
	}

	public Object getResult(String key) {
		return this.result.get(key);
	}


}
