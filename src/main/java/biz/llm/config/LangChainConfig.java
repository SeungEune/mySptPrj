package biz.llm.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * LangChain4j 및 Ollama 연동 설정
 */
@Configuration
public class LangChainConfig {

    @Bean
    public ChatLanguageModel ollamaChatLanguageModel(
            @Value("${local.llm.base-url}") String baseUrl,
            @Value("${local.llm.model}") String modelName,
            @Value("${local.llm.timeout}") Integer timeout
    ) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofMillis(timeout.longValue()))
                .build();
    }
}
