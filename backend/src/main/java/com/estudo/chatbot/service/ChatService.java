package com.estudo.chatbot.service;

import com.estudo.chatbot.config.ChatConfig;
import com.estudo.chatbot.config.OllamaProperties;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatModel chatModel;
    private final ChatMemory memory;
    private final OllamaProperties ollama;

    public ChatService(ChatModel chatModel, ChatMemory memory, OllamaProperties ollama) {
        this.chatModel = chatModel;
        this.memory = memory;
        this.ollama = ollama;
    }

    public String modelName() {
        return ollama.modelName();
    }

    public String baseUrl() {
        return ollama.baseUrl();
    }

    /**
     * 1. Monta a lista: histórico + a fala nova.
     * 2. Pede a resposta ao modelo.
     * 3. Só então grava a fala e a resposta na memória.
     * Se o Ollama falhar, o histórico não fica pela metade.
     */
    public synchronized String reply(String userText) {
        List<ChatMessage> turn = new ArrayList<>(memory.messages());
        turn.add(UserMessage.from(userText));

        log.info("Enviando {} mensagens ao modelo {}", turn.size(), ollama.modelName());

        ChatResponse response;
        try {
            response = chatModel.chat(turn);
        } catch (RuntimeException ex) {
            throw new ModelUnavailableException(
                    "Não consegui falar com o Ollama em " + ollama.baseUrl()
                            + " (modelo " + ollama.modelName() + "). "
                            + "Confira se o Ollama está ligado e se o modelo foi baixado. "
                            + "Detalhe: " + ex.getMessage(),
                    ex);
        }

        String answer = response.aiMessage().text();
        if (answer == null || answer.isBlank()) {
            answer = "(O modelo respondeu sem texto.)";
        }

        memory.add(UserMessage.from(userText));
        memory.add(response.aiMessage());
        return answer;
    }

    public synchronized void reset() {
        memory.clear();
        memory.add(SystemMessage.from(ChatConfig.SYSTEM_PROMPT));
        log.info("Memória da conversa apagada");
    }
}
