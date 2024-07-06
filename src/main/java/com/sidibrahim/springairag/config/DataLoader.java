package com.sidibrahim.springairag.config;

import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Configuration
@Slf4j
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    @Value("classpath:/pdfs/ragPdf.pdf")
    private Resource pdfFile;
    @Value("my-vs.json")
    private String vectorStoreName;
    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        String path = Path.of("src", "main", "resources","vectorstore").toFile().getAbsolutePath() + "/" + vectorStoreName;
        File file = new File(path);
        if (file.exists()) {
            log.info("VECTOR STORE EXISTS => " + file.getAbsolutePath());
            simpleVectorStore.load(file);
        } else {
            PagePdfDocumentReader documentReader = new PagePdfDocumentReader(pdfFile);
            List<Document> documents = documentReader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.split(documents);
            simpleVectorStore.add(chunks);
            simpleVectorStore.save(file);
        }
        return simpleVectorStore;
    }
}
