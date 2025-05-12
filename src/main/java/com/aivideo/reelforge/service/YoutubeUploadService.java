package com.aivideo.reelforge.service;

import com.aivideo.reelforge.dto.ProcessedVideo;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class YoutubeUploadService {
    @Autowired
    private final YouTube youtube;

    private String getUploadedVideoId(ProcessedVideo video) {
        String videoId = null;
        try {
            Video youtubeVideo = new Video()
                    .setSnippet(new VideoSnippet()
                            .setTitle(video.getTitle())
                            .setDescription(video.getDescription())
                            .setTags(video.getTags()))
                    .setStatus(new VideoStatus()
                            .setPrivacyStatus("private"));

            InputStreamContent mediaContent = new InputStreamContent(
                    "video/*",
                    new FileInputStream(video.getVideoFile())
            );

            YouTube.Videos.Insert request = youtube.videos()
                    .insert("snippet,status", youtubeVideo, mediaContent);

            videoId = request.execute().getId();
        }catch (Exception e) {
            log.error("Error occured during uploading video: ", e);
        }
        return videoId;
    }

    public Mono<String> uploadVideo(ProcessedVideo video) {
        return Mono
                .fromCallable(() -> getUploadedVideoId(video))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(videoId -> log.info("Uploaded video ID: {}", videoId))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .doOnError(e -> log.error("Upload failed: ", e));
    }
}
