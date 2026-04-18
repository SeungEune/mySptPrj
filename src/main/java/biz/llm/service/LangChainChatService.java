package biz.llm.service;

import biz.llm.vo.AiChatRequestVO;
import biz.llm.vo.AiChatResponseVO;

/**
 * LangChain4j 기반 AI 채팅 서비스
 */
public interface LangChainChatService {

    AiChatResponseVO sendMessage(AiChatRequestVO requestVO);
}
