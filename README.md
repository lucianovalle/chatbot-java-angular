# Chatbot de estudo (Java + Angular)

Um chat mínimo para ver o caminho de uma mensagem:

1. Você escreve no Angular.
2. O Angular faz `POST /api/chat`.
3. O Spring Boot entrega o texto ao LangChain4j.
4. O LangChain4j manda o histórico ao modelo no Ollama e devolve a resposta.

A memória fica no servidor. O botão **Nova conversa** chama `POST /api/chat/reset` e apaga esse histórico.

## O que abrir primeiro

| Arquivo | O que ele ensina |
| --- | --- |
| `frontend/src/app/chat.service.ts` | As três chamadas HTTP |
| `frontend/src/app/app.component.ts` | Como a tela guarda as mensagens |
| `backend/.../web/ChatController.java` | A API REST |
| `backend/.../config/ChatConfig.java` | O modelo e a memória do LangChain4j |
| `backend/.../service/ChatService.java` | O passo a passo de uma resposta |
| `backend/src/main/resources/application.properties` | Endereço e nome do modelo |

## Como subir

São três terminais. Deixe os três abertos. Para parar um, use Ctrl+C nele. A ordem é Ollama, depois o Java, depois o Angular. No fim, abra `http://localhost:4200`.

O nome do modelo é `llama3.2`, em `backend/src/main/resources/application.properties`. Ele já está baixado. Se um dia faltar, rode `~/.local/bin/ollama pull llama3.2` com o Ollama ligado.

### 1. Ollama

Pasta: `/home/luciano_valle`

```bash
~/.local/bin/ollama serve
```

Fica em `http://localhost:11434`.

### 2. Backend Java

Pasta: `/home/luciano_valle/meu_projeto_python/chatbot-java-angular/backend`

```bash
export JAVA_HOME="$HOME/.local/share/chatbot-learn-tools/jdk/jdk-21.0.13+9"
export PATH="$JAVA_HOME/bin:$HOME/.local/share/chatbot-learn-tools/maven/bin:$PATH"
mvn spring-boot:run
```

Espere a linha `Started ChatbotApplication`. A API fica em `http://localhost:8080`.

### 3. Angular

Pasta: `/home/luciano_valle/meu_projeto_python/chatbot-java-angular/frontend`

```bash
export PATH="$HOME/.local/share/chatbot-learn-tools/node/bin:$PATH"
npm start
```

O `npm install` só é necessário na primeira vez, nessa mesma pasta. O arquivo `proxy.conf.json` encaminha `/api` para a porta 8080.

## Peças do LangChain4j neste exemplo

- `ChatModel` (`OllamaChatModel`): quem gera o texto.
- `ChatMemory` (`MessageWindowChatMemory`): as últimas 20 mensagens. A instrução de sistema fica na frente e não sai quando a janela enche.
- `UserMessage`, `SystemMessage` e `AiMessage`: os três tipos de fala que o modelo recebe.

Há uma memória só para o servidor inteiro. Serve para estudar. Com vários usuários, cada conversa precisaria da própria `ChatMemory`.
