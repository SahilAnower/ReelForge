package com.aivideo.reelforge.service.base;

import reactor.core.publisher.Mono;

public interface TTSService {
    Mono<byte[]> synthesizeSpeech(String text);
}
