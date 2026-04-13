package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 성능 테스트 응답 VO
 */
@Getter
@Setter
public class LlmPerfTestResponseVO {

    private LlmPerfSummaryVO summary;

    private List<LlmPerfResultItemVO> resultList;
}
