package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 단일 성능 테스트 결과 VO
 */
@Getter
@Setter
public class LlmPerfResultItemVO {

    private Integer requestNo;

    private String status;

    private Long elapsedMs;

    private Integer responseLength;

    private String errorMessage;
}
