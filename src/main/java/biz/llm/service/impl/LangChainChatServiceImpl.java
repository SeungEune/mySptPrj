package biz.llm.service.impl;

import biz.llm.dao.LlmChatDAO;
import biz.llm.service.ChatHistoryService;
import biz.llm.service.LangChainAiAssistant;
import biz.llm.service.LangChainChatService;
import biz.llm.service.PromptTemplateService;
import biz.llm.vo.AiChatRequestVO;
import biz.llm.vo.AiChatResponseVO;
import biz.llm.vo.LlmChatSessionCreateVO;
import biz.llm.vo.LlmChatSessionVO;
import biz.util.AuthService;
import biz.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * LangChain4j 기반 AI 채팅 서비스 구현체
 */
@Slf4j
@Service("langChainChatService")
public class LangChainChatServiceImpl implements LangChainChatService {

    private static final int DEFAULT_MAX_MESSAGE_LENGTH = 10000;

    @Resource(name = "llmChatDAO")
    private LlmChatDAO llmChatDAO;

    @Resource
    private AuthService authService;

    @Resource(name = "promptTemplateService")
    private PromptTemplateService promptTemplateService;

    @Resource(name = "chatHistoryService")
    private ChatHistoryService chatHistoryService;

    @Resource
    private LangChainAssistantFactory langChainAssistantFactory;

    @Value("${local.llm.model}")
    private String model;

    @Value("${langchain.chat.max-history:20}")
    private Integer maxHistory;

    @Value("${langchain.chat.max-message-length:10000}")
    private Integer maxMessageLength;

    @Override
    public AiChatResponseVO sendMessage(AiChatRequestVO requestVO) {
        validateRequest(requestVO);

        long responseStartTime = System.currentTimeMillis();
        LlmChatSessionVO sessionVO = ensureChatSession(requestVO);
        String userMessage = requestVO.getMessage().trim();
        String registerId = authService.getCurrentUserId();

        try {
            chatHistoryService.saveChatHistory(sessionVO.getChatSessionId(), "user", userMessage, null, registerId);

            String assistantInput = buildAssistantInput(sessionVO, userMessage);
            LangChainAiAssistant assistant = langChainAssistantFactory.createAssistant();
            String answer = assistant.chat(assistantInput);
            if (StringUtil.isEmpty(answer)) {
                throw new RuntimeException("AI 응답이 비어 있습니다.");
            }

            long elapsedMs = System.currentTimeMillis() - responseStartTime;
            chatHistoryService.saveChatHistory(sessionVO.getChatSessionId(), "assistant", answer, elapsedMs, registerId);
            llmChatDAO.updateChatSessionLastUpdtDt(sessionVO.getChatSessionId());

            AiChatResponseVO responseVO = new AiChatResponseVO();
            responseVO.setAnswer(answer);
            responseVO.setModel(model);
            responseVO.setStatus("ready");
            return responseVO;
        } catch (Exception e) {
            log.error("LangChain4j 기반 로컬 LLM 호출 중 오류 발생, chatSessionId={}", sessionVO.getChatSessionId(), e);
            throw new RuntimeException("AI 서버 호출 중 오류가 발생했습니다.");
        }
    }

    private void validateRequest(AiChatRequestVO requestVO) {
        int limit = maxMessageLength == null || maxMessageLength < 1 ? DEFAULT_MAX_MESSAGE_LENGTH : maxMessageLength;

        if (requestVO == null || StringUtil.isEmpty(requestVO.getMessage())) {
            throw new IllegalArgumentException("질문을 입력해주세요.");
        }

        String message = requestVO.getMessage().trim();
        if (message.length() > limit) {
            throw new IllegalArgumentException("질문은 " + limit + "자 이하로 입력해주세요.");
        }
    }

    private String buildAssistantInput(LlmChatSessionVO sessionVO, String userMessage) {
        String systemPrompt = promptTemplateService.buildSystemPrompt(sessionVO.getPromptRoleCd());
        String conversationContext = buildConversationContext(sessionVO.getChatSessionId());

        StringBuilder builder = new StringBuilder();
        builder.append("[SYSTEM PROMPT]\n");
        builder.append(systemPrompt);
        builder.append("\n\n");

        if (!StringUtil.isEmpty(conversationContext)) {
            builder.append("[CONVERSATION HISTORY]\n");
            builder.append(conversationContext);
            builder.append("\n\n");
        }

        builder.append("[USER MESSAGE]\n");
        builder.append(userMessage);
        return builder.toString();
    }

    private String buildConversationContext(Long chatSessionId) {
        List<biz.llm.vo.AiChatMessageVO> historyList = chatHistoryService.getChatHistoryAsMessageList(chatSessionId);
        int historyStartIndex = Math.max(0, historyList.size() - getSafeMaxHistory());
        StringBuilder builder = new StringBuilder();

        for (int i = historyStartIndex; i < historyList.size(); i++) {
            biz.llm.vo.AiChatMessageVO historyVO = historyList.get(i);
            if (historyVO == null || StringUtil.isEmpty(historyVO.getRole()) || StringUtil.isEmpty(historyVO.getContent())) {
                continue;
            }

            builder.append("[");
            builder.append(historyVO.getRole().toUpperCase());
            builder.append("] ");
            builder.append(historyVO.getContent());
            builder.append("\n");
        }

        return builder.toString().trim();
    }

    private int getSafeMaxHistory() {
        return maxHistory == null || maxHistory < 1 ? 20 : maxHistory;
    }

    private LlmChatSessionVO ensureChatSession(AiChatRequestVO requestVO) {
        if (requestVO.getChatSessionId() != null) {
            LlmChatSessionVO sessionVO = llmChatDAO.selectChatSessionDetail(requestVO.getChatSessionId());
            if (sessionVO != null) {
                return sessionVO;
            }
        }

        LlmChatSessionCreateVO createVO = new LlmChatSessionCreateVO();
        createVO.setPromptRoleCd(StringUtil.isEmpty(requestVO.getPromptRoleCd()) ? "DEV" : requestVO.getPromptRoleCd());
        createVO.setSessionTitle(buildSessionTitle(requestVO.getMessage(), createVO.getPromptRoleCd()));

        LlmChatSessionVO sessionVO = new LlmChatSessionVO();
        sessionVO.setPromptRoleCd(createVO.getPromptRoleCd());
        sessionVO.setSessionTitle(createVO.getSessionTitle());
        sessionVO.setFrstRegisterId(authService.getCurrentUserId());
        llmChatDAO.insertChatSession(sessionVO);
        return llmChatDAO.selectChatSessionDetail(sessionVO.getChatSessionId());
    }

    private String buildSessionTitle(String sessionTitle, String promptRoleCd) {
        String trimmedTitle = sessionTitle == null ? "" : sessionTitle.trim();
        if (trimmedTitle.length() > 30) {
            trimmedTitle = trimmedTitle.substring(0, 30) + "...";
        }

        String roleName = biz.llm.vo.LlmPromptRoleType.fromCode(promptRoleCd).getRoleNm();
        return "[" + roleName + "] " + trimmedTitle;
    }
}
