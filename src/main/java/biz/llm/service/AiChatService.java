package biz.llm.service;

import biz.llm.vo.*;

import java.util.List;

/**
 * AI 채팅 Service
 */
public interface AiChatService {

    AiChatResponseVO sendMessage(AiChatRequestVO requestVO);

    AiChatResponseVO getModelStatus();

    LlmPerfTestResponseVO runPerformanceTest(LlmPerfTestRequestVO requestVO);

    List<LlmPromptRoleVO> getPromptRoleList();

    LlmChatSessionVO createChatSession(LlmChatSessionCreateVO createVO);

    List<LlmChatSessionVO> getChatSessionList();

    LlmChatSessionDetailVO getChatSessionDetail(Long chatSessionId);
}
