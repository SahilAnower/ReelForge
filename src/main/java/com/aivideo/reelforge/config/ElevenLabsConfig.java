package com.aivideo.reelforge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ElevenLabsConfig {
    @Value("$elevenlabs.apikey{}")
    private String apiKey;

    @Value("${elevenlabs.baseurl}")
    private String baseUrl;

    @Bean(name = "elevenLabsWebClient")
    public WebClient elevenLabsWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("xi-api-key", apiKey)
                .defaultHeader(HttpHeaders.ACCEPT, "audio/mpeg")
                .codecs(clientCodecConfigurer -> clientCodecConfigurer
                        .customCodecs()
                        .registerWithDefaultConfig(new Jackson2JsonDecoder()))
                .build();
    }
}
