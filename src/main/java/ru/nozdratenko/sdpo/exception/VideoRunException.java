package ru.nozdratenko.sdpo.exception;

public class VideoRunException extends Exception {
    public String message;
    public VideoRunException(String message) {
        this.message = message;
    }
}
