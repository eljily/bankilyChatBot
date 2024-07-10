package com.sidibrahim.springairag.controllers;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class OpenAiRestController {
    private static final Logger log = LoggerFactory.getLogger(OpenAiRestController.class);
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public OpenAiRestController(ChatClient.Builder chatClient,VectorStore vectorStore) {
        this.chatClient = chatClient.build();
        this.vectorStore = vectorStore;
    }
    private final String myPrompt = """
            You are BankilyChatbot, a virtual assistant developed by Sidibrahim Eljily to help customers with their inquiries about the Bankily service. Please answer their questions as politely and professionally as possible. If the customer seems frustrated or angry, make sure to respond with extra care and politeness.
                        \s
                         When responding, use only the provided context: {context}. If the answer is not in the context, say that you do not know and offer to find out more information. Avoid giving the impression that you are a chatbot.
                        \s
                         Here is the customer's question: {question}""";

    PromptTemplate promptTemplate = new PromptTemplate(myPrompt);

    @GetMapping(value = "/chat/{message}",produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@PathVariable String message){
        List<Document> documents = vectorStore.similaritySearch(
            SearchRequest.query(message).withTopK(5)
        );
        List<String> context = documents.stream().map(Document::getContent).toList();
        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", message));
        log.info("Question : {}", message);
        log.info("Context : {}", context);
        return chatClient.prompt(prompt)
                .call()
            .content();
}
}
