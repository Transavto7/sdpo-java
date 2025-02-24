package ru.nozdratenko.sdpo.InspectionManager.Exceptions;

import org.json.JSONObject;

public class InternalServerError extends Throwable {
    public String message;

    public InternalServerError(String message) {
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
