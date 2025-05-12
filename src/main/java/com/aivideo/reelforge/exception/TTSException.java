package com.aivideo.reelforge.exception;

public class TTSException extends RuntimeException{
    public TTSException(String message) {
        super(message);
    }

    public TTSException(String message, Throwable cause) {
        super(message, cause);
    }
}
