package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * AI 채팅 응답 VO
 */
@Getter
@Setter
public class AiChatResponseVO {

    private String answer;

    private String model;

    private String status;
}
