package biz.llm.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * LLM 채팅 세션 생성 요청 VO
 */
@Getter
@Setter
public class LlmChatSessionCreateVO {

    private String sessionTitle;

    private String promptRoleCd;
}
