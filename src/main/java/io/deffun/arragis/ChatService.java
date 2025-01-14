package io.deffun.arragis;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private static final String PROMPT_BLUEPRINT = """
            Answer the query strictly referring the provided context:
            {context}
            Query:
            {query}
            In case you don't have any answer from the context provided, just say:
            I'm sorry I don't have the information you are looking for.
            """;

    private final ChatModel chatClient;
    private final VectorStore vectorStore;

    public ChatService(@Qualifier("openAiChatModel") ChatModel chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public ChatResponse chat(String query) {
        List<Document> context = vectorStore.similaritySearch(query);
        String prompt = createPrompt(query, context);
        return new ChatResponse(chatClient.call(prompt));
    }

    private String createPrompt(String query, List<Document> context) {
        PromptTemplate promptTemplate = new PromptTemplate(PROMPT_BLUEPRINT);
        promptTemplate.add("query", query);
        promptTemplate.add("context", context);
        return promptTemplate.render();
    }
}
