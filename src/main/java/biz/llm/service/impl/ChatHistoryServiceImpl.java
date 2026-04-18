package biz.llm.service.impl;

import biz.llm.dao.LlmChatDAO;
import biz.llm.service.ChatHistoryService;
import biz.llm.vo.AiChatMessageVO;
import biz.llm.vo.LlmChatHistoryVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 채팅 이력 조회 및 저장 구현체
 */
@Service("chatHistoryService")
public class ChatHistoryServiceImpl implements ChatHistoryService {

    @Resource(name = "llmChatDAO")
    private LlmChatDAO llmChatDAO;

    @Override
    public List<LlmChatHistoryVO> getChatHistoryList(Long chatSessionId) {
        return llmChatDAO.selectChatHistoryList(chatSessionId);
    }

    @Override
    public List<AiChatMessageVO> getChatHistoryAsMessageList(Long chatSessionId) {
        return llmChatDAO.selectChatHistoryList(chatSessionId).stream()
                .map(this::toMessageVO)
                .collect(Collectors.toList());
    }

    @Override
    public void saveChatHistory(Long chatSessionId, String messageRole, String messageContent, Long responseTimeMs, String registerId) {
        LlmChatHistoryVO historyVO = new LlmChatHistoryVO();
        historyVO.setChatSessionId(chatSessionId);
        historyVO.setMessageSeq(Optional.ofNullable(llmChatDAO.selectNextMessageSeq(chatSessionId)).orElse(1));
        historyVO.setMessageRole(messageRole);
        historyVO.setMessageContent(messageContent);
        historyVO.setResponseTimeMs(responseTimeMs);
        historyVO.setFrstRegisterId(registerId);
        llmChatDAO.insertChatHistory(historyVO);
    }

    private AiChatMessageVO toMessageVO(LlmChatHistoryVO historyVO) {
        AiChatMessageVO messageVO = new AiChatMessageVO();
        messageVO.setRole(historyVO.getMessageRole());
        messageVO.setContent(historyVO.getMessageContent());
        return messageVO;
    }
}
