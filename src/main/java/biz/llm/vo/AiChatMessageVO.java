package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * AI 채팅 메시지 VO
 */
@Getter
@Setter
public class AiChatMessageVO {

    private String role;

    private String content;
}
