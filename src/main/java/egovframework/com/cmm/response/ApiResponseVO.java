package egovframework.com.cmm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * API 요청 결과 VO
 */
@Getter 
@AllArgsConstructor
public class ApiResponseVO<T> {

    // 응답 상태
    private final StatusVO status;
    
    // 응답 결과
    private T result;

    public static <T> ResponseEntity apiResponse(@Nullable List<T> param, @Nullable PagingVO pagingVO, int code, String message) {
        ApiResponseVO apiResponse = new ApiResponseVO<>(param, pagingVO, code, message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    public static <T> ResponseEntity apiResponse(@Nullable List<T> param, @Nullable PagingVO pagingVO, String message) {
        return apiResponse(param, pagingVO, HttpStatus.OK.value(), message);
    }

    public static <T> ResponseEntity apiResponse(@Nullable List<T> param, @Nullable PagingVO pagingVO) {
        return apiResponse(param, pagingVO, HttpStatus.OK.value(), "응답 성공");
    }

    public static <T> ResponseEntity apiResponse(@Nullable T param, int code, String message) {
        ApiResponseVO apiResponse = new ApiResponseVO(param, code, message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    public static <T> ResponseEntity apiResponse(@Nullable T param) {
        return apiResponse(param, HttpStatus.OK.value(), "응답 성공");
    }

    public static ResponseEntity apiResponse() {
        return apiResponse(null, HttpStatus.OK.value(), "응답 성공");
    }

    public ApiResponseVO(final T param, final int code, final String message) {
        this.status = new StatusVO(code, message);
        this.result = param;
    }

    public ApiResponseVO(final List<T> param, @Nullable PagingVO pagingVO, final int code, final String message) {
        this.status = new StatusVO(code, message);
        this.result = (T) new ResultListVO(param, pagingVO);
    }
}
