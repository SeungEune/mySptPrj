package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * LLM 성능 테스트 요청 VO
 */
@Getter
@Setter
public class LlmPerfTestRequestVO {

    private String prompt;

    private Integer concurrency;

    private Integer requestCount;
}
