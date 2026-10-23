package com.estudo.chatbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Lê o bloco chat.ollama do application.properties.
 * O Spring preenche este record na subida da aplicação.
 */
@ConfigurationProperties(prefix = "chat.ollama")
public record OllamaProperties(String baseUrl, String modelName, int timeoutSeconds) {
}
