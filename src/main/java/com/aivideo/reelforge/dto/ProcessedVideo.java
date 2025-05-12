package com.aivideo.reelforge.dto;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.List;

@Data
@Builder
public class ProcessedVideo {
    private File videoFile;
    private String title;
    private String description;
    private List<String> tags;
    private String audioFilePath;
    private String videoFilePath;
}
