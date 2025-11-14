package egovframework.com.cmm.response;

import egovframework.com.cmm.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private StatusVO status;

    protected ErrorResponse(final egovframework.com.cmm.ErrorCode code, String message) {
        this.status = new StatusVO(code.getCode(), message);
    }

    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code, code.getMessage());
    }

    public static ErrorResponse of(final ErrorCode code, String message) {
        return new ErrorResponse(code, message);
    }
}
