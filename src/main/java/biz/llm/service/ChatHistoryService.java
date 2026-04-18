package biz.llm.service;

import biz.llm.vo.AiChatMessageVO;
import biz.llm.vo.LlmChatHistoryVO;

import java.util.List;

/**
 * 채팅 이력 조회 및 저장 서비스
 */
public interface ChatHistoryService {

    List<LlmChatHistoryVO> getChatHistoryList(Long chatSessionId);

    List<AiChatMessageVO> getChatHistoryAsMessageList(Long chatSessionId);

    void saveChatHistory(Long chatSessionId, String messageRole, String messageContent, Long responseTimeMs, String registerId);
}
