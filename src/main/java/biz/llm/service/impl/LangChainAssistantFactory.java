package biz.llm.service.impl;

import biz.llm.service.LangChainAiAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * LangChain4j AI Service 생성 팩토리
 */
@Component
public class LangChainAssistantFactory {

    @Resource(name = "ollamaChatLanguageModel")
    private ChatLanguageModel chatLanguageModel;

    public LangChainAiAssistant createAssistant() {
        return AiServices.create(LangChainAiAssistant.class, chatLanguageModel);
    }
}
