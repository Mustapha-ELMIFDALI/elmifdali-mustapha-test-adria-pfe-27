package org.elmifdali.chatbotservice.config;

import com.google.genai.errors.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Map;

@Configuration
public class AiConfig {

    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);
    private static final int TOO_MANY_REQUESTS = 429;

    // Gemini only accepts JSON tool results, so a refused operation must be returned as JSON too
    @Bean
    ToolExecutionExceptionProcessor toolExecutionExceptionProcessor(JsonMapper jsonMapper) {
        return exception -> jsonMapper.writeValueAsString(Map.of("error", exception.getMessage()));
    }

    // Retries the Gemini call only, never the tool: repeating a tool could move the money twice
    @Bean
    RetryTemplate retryTemplate() {
        return new RetryTemplate(RetryPolicy.builder()
                .maxRetries(4)
                .delay(Duration.ofSeconds(15))
                .predicate(AiConfig::isRateLimit)
                .build());
    }

    private static boolean isRateLimit(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof ApiException apiException && apiException.code() == TOO_MANY_REQUESTS) {
                log.warn("Gemini rate limit reached, retrying");
                return true;
            }
        }
        return false;
    }
}
