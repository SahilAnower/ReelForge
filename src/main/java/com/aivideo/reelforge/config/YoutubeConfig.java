package com.aivideo.reelforge.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;

@Configuration
public class YoutubeConfig {
    @Bean
    public YouTube youtubeClient() {
        try {
            InputStream credStream = new FileInputStream("service-account.json");
            GoogleCredential credential = GoogleCredential.fromStream(credStream)
                    .createScoped(Collections.singleton(YouTubeScopes.YOUTUBE_UPLOAD));

            return new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("reelforge")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Youtube client: ", e);
        }
    }
}
