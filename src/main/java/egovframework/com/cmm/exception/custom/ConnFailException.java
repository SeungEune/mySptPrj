package egovframework.com.cmm.exception.custom;

import egovframework.com.cmm.ErrorCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : 김언중
 * @since : 2023. 11. 15.
 * <p>
 * == 수정사항 ==
 * ---------------------------------------
 * 2023. 11. 15.  김언중 최초 생성
 */
@Getter @ToString
public class ConnFailException extends RuntimeException {

    private final ErrorCode code;

    public ConnFailException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}
