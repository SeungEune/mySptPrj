package egovframework.com.cmm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 처리 결과를 담는 VO 클래스
 * @author Spatialt 개발팀
 * @since 2025.11.24
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultVO {

    private String resultMessage;
    private boolean resultValue;
    private String statusCode;
    private String message;
}

