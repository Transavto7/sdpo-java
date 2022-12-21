package ru.nozdratenko.sdpo.exception;

import org.json.JSONObject;

public class ApiException  extends Throwable {
    public String message;
    public ApiException(String message) {
        this.message = message;
    }

    public JSONObject getResponse() {
        JSONObject json = new JSONObject();
        json.put("message", this.message);
        return json;
    }
}
