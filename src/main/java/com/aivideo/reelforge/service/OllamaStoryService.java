package com.aivideo.reelforge.service;

import com.aivideo.reelforge.constants.ModelConstants;
import com.aivideo.reelforge.dto.OllamaResponse;
import com.aivideo.reelforge.service.base.StoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
public class OllamaStoryService implements StoryService {
    private final WebClient webClient;
    @Value("${ollama.baseurl}")
    private String ollamaBaseUrl;
    @Value("${ollama.model}")
    private String ollamaModel;
    @Value("${ollama.creativity}")
    private Double ollamaCreativity;
    @Value("${ollama.context.length}")
    private Integer ollamaContextLength;
    @Value("${ollama.timeout}")
    private Integer ollamaTimeout;
    @Value("${ollama.retry.count}")
    private Integer ollamaRetryCount;

    public OllamaStoryService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(ollamaBaseUrl)
                .codecs(clientCodecConfigurer -> clientCodecConfigurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    @Override
    public Mono<String> generateStory(String genre, String theme) {
        String formattedPrompt = String.format(
                ModelConstants.STORY_PROMPT_TEMPLATE,
                genre,
                theme
        );
        return webClient
                .post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", ollamaModel,
                        "prompt", formattedPrompt,
                        "stream", false,
                        "options", Map.of(
                                "temperature", ollamaCreativity,
                                "num_ctx", ollamaContextLength
                        )
                ))
                .retrieve()
                .bodyToMono(OllamaResponse.class)
                .map(OllamaResponse::getResponse)
                .doOnSubscribe(subscription -> log.info("Generating {} story about {}", genre, theme))
                .timeout(Duration.ofSeconds(ollamaTimeout))
                .retryWhen(Retry.backoff(ollamaRetryCount, Duration.ofSeconds(2)));
    }
}
