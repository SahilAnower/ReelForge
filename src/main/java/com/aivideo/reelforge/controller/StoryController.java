package com.aivideo.reelforge.controller;

import com.aivideo.reelforge.service.base.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stories")
public class StoryController {

    private final StoryService storyService;

    @GetMapping("/generate")
    public Mono<Map<String, String>> generateStory(
        @RequestParam(defaultValue = "fantasy") String genre,
        @RequestParam(defaultValue = "dragons") String theme
    ) {
        return storyService
                .generateStory(genre, theme)
                .map(content -> Map.of(
                        "genre", genre,
                        "theme", theme,
                        "content", content
                ));
    }

}
