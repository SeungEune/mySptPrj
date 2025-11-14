package egovframework.com.cmm.handler.exceptionHandler;


import com.fasterxml.jackson.core.JsonParseException;
import egovframework.com.cmm.ErrorCode;
import egovframework.com.cmm.exception.custom.BadRequestException;
import egovframework.com.cmm.exception.custom.NoContentException;
import egovframework.com.cmm.exception.custom.UnauthorizedException;
import egovframework.com.cmm.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.time.format.DateTimeParseException;

/**
 * [공통] 예외 처리
 * @author 김민석
 * @since 2023.09.05
 * @version 1.0
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일       수정자      수정내용
 *  ----------    ------      ---------------------------
 *  2023.09.05    김민석      최초 생성
 *
 * </pre>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final HttpStatus HTTP_STATUS_OK = HttpStatus.OK;

    /**
     * [Exception] API 호출 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우
     * @param ex MethodArgumentNotValidException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.debug("handleMethodArgumentNotValidException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] API 호출 시 'Header' 내에 데이터 값이 유효하지 않은 경우
     * @param ex MissingRequestHeaderException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.debug("MissingRequestHeaderException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_HEADER_ERROR);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] API 호출 시 'Header' 내에 데이터 값이 유효하지 않은 경우
     * @param ex MissingRequestHeaderException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(DateTimeParseException.class)
    protected ResponseEntity<ErrorResponse> handleDateTimeParseException(DateTimeParseException ex) {
        log.debug("DateTimeParseException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] 잘못된 서버 요청일 경우 발생한 경우
     * @param e BadRequestException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        log.debug("BadRequestException", e);
        e.printStackTrace();
        final ErrorResponse response = ErrorResponse.of(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] 권한이 없는 요청이 발생한 경우
     * @param e UnauthorizedException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        log.debug("UnauthorizedException", e);
        final ErrorResponse response = ErrorResponse.of(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] 조회된 데이터가 없는 경우
     * @param e NoContentException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(NoContentException.class)
    protected ResponseEntity<ErrorResponse> handleNoContentException(NoContentException e) {
        log.debug("NoContentException", e);
        final ErrorResponse response = ErrorResponse.of(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] 잘못된 주소로 요청 한 경우
     * @param e NoHandlerFoundException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoHandlerFoundExceptionException(NoHandlerFoundException e) {
        log.debug("handleNoHandlerFoundExceptionException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] NULL 값이 발생한 경우
     * @param e NullPointerException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.debug("handleNullPointerException", e);
        e.printStackTrace();
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NULL_POINT_ERROR);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] Input / Output 내에서 발생한 경우
     * @param ex IOException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.debug("handleIOException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.IO_ERROR);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /**
     * [Exception] com.google.gson 내에 Exception 발생하는 경우
     * @param ex JsonParseException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<ErrorResponse> handleJsonParseExceptionException(JsonParseException ex) {
        log.debug("handleJsonParseExceptionException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.JSON_PARSE_ERROR);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    // ==================================================================================================================

    /**
     * [Exception] 나머지 Exception 경우 발생
     * @param ex JsonProcessingException
     * @return ex) ResponseEntity<ErrorResponse>
     * @throws Exception
     */
    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.debug("Exception", ex);
        ex.printStackTrace();
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    public static ResponseEntity<ErrorResponse> makeErrorResponse(ErrorResponse response, HttpStatus status) {
        return new ResponseEntity<>(response, status);
    }
}
