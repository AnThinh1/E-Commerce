package com.codegym.ecommercemanage.config;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Google's Generative AI client.
 * Provides a bean for the Gemini API client.
 */
@Configuration
public class GenAIConfiguration {

    /**
     * Creates and configures a Google GenAI client bean.
     *
     * @param apiKey The Gemini API key from application properties
     * @return Configured Google GenAI client
     */
    @Bean
    public Client genaiClient(@Value("${gemini.api.key}") String apiKey) {
        return Client.builder()
                .apiKey(apiKey)
                .build();
    }
}
