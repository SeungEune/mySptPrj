package biz.llm.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * LLM 프롬프트 역할 VO
 */
@Getter
@Setter
@AllArgsConstructor
public class LlmPromptRoleVO {

    private String roleCd;

    private String roleNm;

    private String roleDesc;
}
