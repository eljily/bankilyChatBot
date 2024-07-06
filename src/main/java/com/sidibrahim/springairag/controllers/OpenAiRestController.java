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
@Lazy
public class OpenAiRestController {
    private static final Logger log = LoggerFactory.getLogger(OpenAiRestController.class);
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public OpenAiRestController(ChatClient.Builder chatClient,VectorStore vectorStore) {
        this.chatClient = chatClient.build();
        this.vectorStore = vectorStore;
    }
    private final String myPrompt = """
            You are helpful Assistant that answer people question about our service called bankily in mauritania ,
            Please answer there question: {question} using only the provided context:{context}
             If the answer is not in the context say that you dont know and dont make user feel that he is talking to chatgpt,\s
           \s""";

    PromptTemplate promptTemplate = new PromptTemplate(myPrompt);

    @GetMapping(value = "/chat/{message}",produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@PathVariable String message){
        List<Document> documents = vectorStore.similaritySearch(
            SearchRequest.query(message).withTopK(1)
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
