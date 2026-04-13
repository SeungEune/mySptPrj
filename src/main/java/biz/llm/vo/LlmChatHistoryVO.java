package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * LLM 채팅 이력 VO
 */
@Getter
@Setter
public class LlmChatHistoryVO {

    private Long chatHistoryId;

    private Long chatSessionId;

    private Integer messageSeq;

    private String messageRole;

    private String messageContent;

    private Long responseTimeMs;

    private String frstRegisterId;

    private String frstRegistDt;
}
