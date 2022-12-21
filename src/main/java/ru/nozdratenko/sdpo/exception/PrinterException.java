package ru.nozdratenko.sdpo.exception;

import org.json.JSONObject;

public class PrinterException extends Throwable {
    public String message;
    public PrinterException(String message) {
        this.message = message;
    }

    public JSONObject getResponse() {
        JSONObject json = new JSONObject();
        json.put("message", this.message);
        return json;
    }
}
