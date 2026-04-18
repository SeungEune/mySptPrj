package biz.llm.service;

/**
 * 프롬프트 역할별 system prompt 생성 서비스
 */
public interface PromptTemplateService {

    String buildSystemPrompt(String promptRoleCd);
}
