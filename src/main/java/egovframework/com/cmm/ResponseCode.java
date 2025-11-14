package egovframework.com.cmm;

public enum ResponseCode {

	/**
	 * ******************************* Global Error CodeList ***************************************
	 * HTTP STATUS Code
	 * 204 : No Content
	 * 304 : Not Modified
	 * 400 : Bad Request
	 * 401 : Unauthorized
	 * 403 : Forbidden
	 * 404 : Not Found
	 * 406 : Not Acceptable
	 * 408 : Request Timeout
	 * 413 : Payload Too Large
	 * 414 : URI Too Long
	 * 429 : Too Many Requests
	 * 500 : Internal Server Error
	 * 501 : Not Implemented
	 * 502 : Bad Gateway
	 * 503 : Service Unavailable
	 * 504 : Gateway Timeout
	 * 505 : HTTP Version not supported
	 */

	SUCCESS(200, "성공했습니다."),
	NO_CONTENT(204,  "조회된 내용이 없습니다."),
	NOT_MODIFIED(304,  "수정되지 않았습니다."),
	BAD_REQUEST(400,  "잘못된 요청입니다."),
	UNAUTHORIZED(401,  "권한이 없습니다."),
	AUTH_ERROR(403, "인가된 사용자가 아닙니다."),
	NOT_FOUND(404,  "서비스를 찾을 수 없습니다."),
	NOT_ACCEPTABLE(406,  "반영되지 않았습니다."),
	REQUEST_TIMEOUT(408,  "요청에 대한 응답이 없습니다."),
	PAYLOAD_TOO_LARGE(413,  "요청 Parameter가 너무 큽니다."),
	URI_TOO_LONG(414,  "URI가 너무 깁니다."),
	TOO_MANY_REQUESTS(429,  "Too Many Requests"),
	INTERNAL_SERVER_ERROR(500,  "오류가 발생하였습니다."),
	NOT_IMPLEMENTED(501,  "수행되지 않았습니다."),
	BAD_GATEWAY(502,  "Bad Gateway"),
	SERVICE_UNAVAILABLE(503,  "일시적으로 서비스를 사용할 수 없습니다."),
	GATEWAY_TIMEOUT(504,  "Gateway Timeout"),
	HTTP_VERSION_NOT_SUPPORTED(505,  "지원되지 않는 HTTP 버전입니다."),

	// Custom Error Code-----------------------------
	// @RequestBody 데이터 미 존재
	DELETED_RULE_TRIGGER(400,  "삭제된 대상을 포함하는 이벤트 룰입니다."),

	REQUEST_BODY_MISSING_ERROR(400,  "Required request body is missing"),

	// 유효하지 않은 타입
	INVALID_TYPE_VALUE(400,  "유효하지 않은 타입입니다."),

	// Request Parameter 로 데이터가 전달되지 않을 경우
	MISSING_REQUEST_PARAMETER_ERROR(400,  "Missing Servlet RequestParameter Exception"),

	// 입력/출력 값이 유효하지 않음
	IO_ERROR(400,  "입/출력 오류가 발생하였습니다."),

	// com.google.gson JSON 파싱 실패
	JSON_PARSE_ERROR(400,  "유효하지 않은 JSON 형식입니다."),

	// com.fasterxml.jackson.core Processing Error
	JACKSON_PROCESS_ERROR(400,  "com.fasterxml.jackson.core Exception"),

	// NULL Point Exception 발생
	NULL_POINT_ERROR(404,  "Null Point Exception"),

	// @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
	NOT_VALID_ERROR(404,  "handle Validation Exception"),

	// @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
	NOT_VALID_HEADER_ERROR(404,  "Header에 데이터가 존재하지 않습니다."),

	CONN_FAIL_ERROR(500,  "외부 서비스 연계에 실패하였습니다."),
	BUSINESS_EXCEPTION(500,  "Business Exception Error"),
	INSERT_ERROR(500,  "오류가 발생하여 저장되지 않았습니다."),
	UPDATE_ERROR(500,  "오류가 발생하여 수정되지 않았습니다."),
	DELETE_ERROR(500,  "오류가 발생하여 삭제되지 않았습니다."),
	SAVE_ERROR(800, "저장시 내부 오류가 발생했습니다."),
	INPUT_CHECK_ERROR(900, "입력값 무결성 오류 입니다.");
	// Custom Error Code-----------------------------

	private int code;
	private String message;

	private ResponseCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
