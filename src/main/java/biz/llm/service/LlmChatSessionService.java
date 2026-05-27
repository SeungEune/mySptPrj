package biz.llm.service;

import biz.llm.vo.LlmChatSessionCreateVO;
import biz.llm.vo.LlmChatSessionDetailVO;
import biz.llm.vo.LlmChatSessionVO;
import biz.llm.vo.LlmPromptRoleVO;

import java.util.List;

/**
 * LLM 채팅 세션 및 역할 조회 서비스
 */
public interface LlmChatSessionService {

    List<LlmPromptRoleVO> getPromptRoleList();

    LlmChatSessionVO createChatSession(LlmChatSessionCreateVO createVO);

    List<LlmChatSessionVO> getChatSessionList();

    LlmChatSessionDetailVO getChatSessionDetail(Long chatSessionId);
}
