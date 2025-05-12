package com.aivideo.reelforge.service.base;

import reactor.core.publisher.Mono;

public interface StoryService {
    Mono<String> generateStory(String genre, String theme);
}
