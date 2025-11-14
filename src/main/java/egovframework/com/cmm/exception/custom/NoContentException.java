package egovframework.com.cmm.exception.custom;

import egovframework.com.cmm.ErrorCode;
import lombok.Getter;

/**
 * @author : 김언중
 * @since : 2024. 07. 27.
 * <p>
 * == 수정사항 ==
 * ---------------------------------------
 * 2024. 07. 27.  김언중 최초 생성
 */
@Getter
public class NoContentException extends RuntimeException {

    private final ErrorCode code = ErrorCode.NO_CONTENT;

    public NoContentException() {
        super(ErrorCode.NO_CONTENT.getMessage());
    }

    public NoContentException(String message) {
        super(message);
    }
}
