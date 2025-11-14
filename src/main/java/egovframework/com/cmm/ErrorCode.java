package egovframework.com.cmm;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
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
    NO_CONTENT(HttpStatus.NO_CONTENT.value(),  "조회된 데이터가 없습니다."),
    NOT_MODIFIED(HttpStatus.NOT_MODIFIED.value(),  "Not Modified"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(),  "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(),  "권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED.value(),  "AccessDenied"),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(),  "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(),  "Not Found"),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE.value(),  "Not Acceptable"),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT.value(),  "Request Timeout"),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE.value(),  "Payload Too Large"),
    URI_TOO_LONG(HttpStatus.URI_TOO_LONG.value(),  "URI Too Long"),
    INSUFFICIENT_SPACE_ON_RESOURCE(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE.value(),  "서버 디스크 공간이 충분하지 않습니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS.value(),  "Too Many Requests"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),  "Internal Server Error"),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED.value(),  "Not Implemented"),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY.value(),  "Bad Gateway"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(),  "Service Unavailable"),
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT.value(),  "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(HttpStatus.HTTP_VERSION_NOT_SUPPORTED.value(),  "HTTP Version not supported"),

    // Custom Error Code
    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST.value(),  "유효하지 않은 타입"),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST.value(),  "JsonParseException"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(HttpStatus.BAD_REQUEST.value(),  "Parameter가 유효하지 않습니다."),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(HttpStatus.BAD_REQUEST.value(),  "유효하지 않은 헤더"),

    // 입력/출력 값이 유효하지 않음
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),  "파일 입/출력에 실패하엿습니다."),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Null Point Exception"),

    BUSINESS_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(),  "Business Exception Error");

    // 에러 코드의 '코드 상태'을 반환한다.
    private final int code;

    // 에러 코드의 '코드 메시지'을 반환한다.
    private final String message;

    ErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
