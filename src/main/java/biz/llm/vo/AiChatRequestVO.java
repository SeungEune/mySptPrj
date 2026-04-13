package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * AI 채팅 요청 VO
 */
@Getter
@Setter
public class AiChatRequestVO {

    private Long chatSessionId;

    private String promptRoleCd;

    private String message;

    private List<AiChatMessageVO> history;
}
