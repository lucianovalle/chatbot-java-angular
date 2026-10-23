package com.estudo.chatbot.config;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    /**
     * Instrução fixa enviada junto com o histórico.
     * O modelo lê isso antes das falas do usuário.
     */
    public static final String SYSTEM_PROMPT = """
            Você é um assistente de estudo, paciente e direto.
            Responda sempre em português, em poucas frases.
            Se não souber, diga que não sabe.
            """;

    /**
     * O modelo. Aqui escolhemos o Ollama local.
     * Para usar outro servidor, mude baseUrl e modelName no application.properties.
     */
    @Bean
    ChatModel chatModel(OllamaProperties properties) {
        return OllamaChatModel.builder()
                .baseUrl(properties.baseUrl())
                .modelName(properties.modelName())
                .temperature(0.7)
                .timeout(Duration.ofSeconds(properties.timeoutSeconds()))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /**
     * Memória da conversa: guarda as últimas 20 mensagens.
     * A mensagem de sistema fica sempre na primeira posição e não é apagada
     * quando a janela enche.
     * Este bean é único para o servidor inteiro: serve para um aluno estudando
     * sozinho. Num app com vários usuários, cada um teria a sua memória.
     */
    @Bean
    ChatMemory chatMemory() {
        ChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .alwaysKeepSystemMessageFirst(true)
                .build();
        memory.add(SystemMessage.from(SYSTEM_PROMPT));
        return memory;
    }
}
