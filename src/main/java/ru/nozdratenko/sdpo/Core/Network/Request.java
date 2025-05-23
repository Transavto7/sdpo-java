package ru.nozdratenko.sdpo.Core.Network;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.ApiNotFoundException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request {
    protected URL url;
    protected HttpURLConnection connection;
    protected String method = "POST";
    public boolean success = true;
    protected int responseCode;

    public Request(String url) throws IOException {
        String baseURL = Sdpo.connectionConfig.getString("url");

        if (!baseURL.endsWith("/") && !url.startsWith("/")) {
            baseURL += "/";
        }
        this.url = new URL(baseURL + url);
    }

    public Request(URL url) throws IOException {
        this.url = url;
    }

    public String sendGet() throws IOException, ApiException {
        return sendGet(new HashMap<>());
    }

    public String sendGet(Map<String, String> parameters) throws IOException, ApiException {
        this.url = new URL(this.url.toString() + ParameterStringBuilder.getParamsString(parameters));

        this.connection = this.getConnection(this.url);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Length", "1");
        connection.setRequestProperty("Authorization", "Bearer " + Sdpo.connectionConfig.getString("token"));
        connection.setDoOutput(true);

        InputStream inputStream;
        int status = connection.getResponseCode();

        if (status < 400) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        if (inputStream == null) {
            throw new ApiException("Ошибка запроса. Неверный ответ");
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        connection.disconnect();

        int responseCode = connection.getResponseCode();

        if (responseCode == 404) {
            throw new ApiNotFoundException(this.url.toString());
        }

        if (200 > responseCode || responseCode > 299) {
            String message = "Неизвестная ошибка запроса";
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                if (jsonObject.has("message")) {
                    message = jsonObject.getString("message");
                } else {
                    message = "Ошибка запроса. Неизвестный ответ";
                }
            } catch (Exception e) {
                SdpoLog.error("Exception in response " + e);
            }

            throw new ApiException(message);
        }

        return response.toString();
    }

    public String sendPost() throws IOException, ApiException {
        return sendPost("");
    }

    public String sendPost(String json) throws IOException, ApiException {

        InputStream inputStream = this.sendPostInputStream(json);

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        connection.disconnect();

        this.setResponseCode(connection.getResponseCode());

        if (this.getResponseCode() == 404) {
            throw new ApiNotFoundException(this.url.toString());
        }

        if (200 > this.getResponseCode() || this.getResponseCode() > 299) {
            String message = "Неизвестная ошибка запроса.";
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                if (jsonObject.has("message")) {
                    message = jsonObject.getString("message");
                } else {
                    message = "Ошибка запроса. Неизвестный ответ.";
                }
            } catch (Exception e) {
            }
            SdpoLog.error("Failed to send response to url: " + this.url);
            throw new ApiException(message);
        }

        return response.toString();
    }

    public InputStream sendPostInputStream(String json) throws IOException, ApiException {
        this.connection = this.getConnection(this.url);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", "1");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Authorization", "Bearer " + Sdpo.connectionConfig.getString("token"));
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        InputStream inputStream;

        this.setResponseCode(connection.getResponseCode());

        if (this.getResponseCode() < 400) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        if (inputStream == null) {
            throw new ApiException("Ошибка запроса. Неверный ответ");
        }

        return inputStream;
    }

    public URL getUrl() {
        return url;
    }

    private HttpURLConnection getConnection(URL url) throws IOException {
        if (Objects.equals(url.getProtocol(), "http")) {
            return (HttpURLConnection) url.openConnection();
        }

        return (HttpsURLConnection) url.openConnection();
    }


    protected void setResponseCode(int code) {
        this.responseCode = code;
    }

    public int getResponseCode() {
        return this.responseCode;
    }
}
