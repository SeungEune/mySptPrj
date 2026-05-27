package biz.llm.service;

import biz.llm.vo.AiChatRequestVO;
import biz.llm.vo.AiChatResponseVO;
import biz.llm.vo.LlmPerfTestRequestVO;
import biz.llm.vo.LlmPerfTestResponseVO;

/**
 * AI 채팅 Service
 */
public interface AiChatService {

    AiChatResponseVO sendMessage(AiChatRequestVO requestVO);

    AiChatResponseVO getModelStatus();

    LlmPerfTestResponseVO runPerformanceTest(LlmPerfTestRequestVO requestVO);

}
