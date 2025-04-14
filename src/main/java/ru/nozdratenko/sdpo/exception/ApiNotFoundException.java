package ru.nozdratenko.sdpo.exception;

public class ApiNotFoundException extends ApiException {
    public String url;
    public ApiNotFoundException(String url) {
        super("Resource not found " + url);
    }
}
