package biz.llm.vo;

import egovframework.com.cmm.vo.ComDefaultVO;
import lombok.Getter;
import lombok.Setter;

/**
 * LLM 채팅 세션 VO
 */
@Getter
@Setter
public class LlmChatSessionVO extends ComDefaultVO {

    private Long chatSessionId;

    private String sessionTitle;

    private String promptRoleCd;

    private String frstRegisterId;

    private String frstRegistDt;

    private String lastUpdtDt;

    private String useYn;
}
