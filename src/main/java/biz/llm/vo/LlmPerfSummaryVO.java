package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 성능 테스트 요약 VO
 */
@Getter
@Setter
public class LlmPerfSummaryVO {

    private Integer concurrency;

    private Integer requestCount;

    private Integer successCount;

    private Integer failCount;

    private Long minElapsedMs;

    private Long maxElapsedMs;

    private Long avgElapsedMs;

    private Double successRate;

    private String model;
}
