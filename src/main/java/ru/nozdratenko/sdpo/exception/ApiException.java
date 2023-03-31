package ru.nozdratenko.sdpo.exception;

import org.json.JSONObject;

public class ApiException extends SdpoException {
    public String message;
    public ApiException(String message) {
        super(message);
    }
}
