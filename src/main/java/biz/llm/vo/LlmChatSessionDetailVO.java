package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * LLM 채팅 세션 상세 VO
 */
@Getter
@Setter
public class LlmChatSessionDetailVO {

    private LlmChatSessionVO sessionInfo;

    private List<LlmChatHistoryVO> historyList;
}
