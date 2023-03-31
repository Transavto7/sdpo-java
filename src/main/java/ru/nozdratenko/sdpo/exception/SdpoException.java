package ru.nozdratenko.sdpo.exception;

import org.json.JSONObject;

public class SdpoException extends Throwable {
    public String message;

    public SdpoException(String message) {
        this.message = message;
    }

    public JSONObject getResponse() {
        JSONObject json = new JSONObject();
        json.put("message", this.message);
        return json;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
