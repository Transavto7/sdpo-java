package ru.nozdratenko.sdpo.network;

import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Request {
    protected URL url;
    protected HttpsURLConnection connection;
    protected String method = "POST";
    public boolean success = true;

    public Request(String url) throws IOException {
        String baseURL = Sdpo.mainConfig.getString("url");

        if (!baseURL.endsWith("/") && !url.startsWith("/")) {
            baseURL += "/";
        }

        this.url = new URL(baseURL + url);
    }

    public Request(URL url) throws IOException {
        this.url = url;
    }

    public String sendGet() throws IOException {
        return sendGet(new HashMap<>());
    }
    public String sendGet(Map<String, String> parameters) throws IOException {
        this.url = new URL(this.url.toString() + ParameterStringBuilder.getParamsString(parameters));

        SdpoLog.info("Request get to: " + url.toString());

        this.connection = (HttpsURLConnection)  this.url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Length", "1");
        connection.setRequestProperty("Authorization", "Bearer " + Sdpo.mainConfig.getString("token"));
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        SdpoLog.info("Response code: " + responseCode);

        InputStream inputStream;
        if (200 <= responseCode && responseCode <= 299) {
            inputStream = connection.getInputStream();
        } else {
            System.out.println(connection.getRequestMethod() + " " + responseCode);
            this.success = false;
            inputStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        connection.disconnect();
        SdpoLog.info("Response: " + response.toString());
        return response.toString();
    }

    public String sendPost() throws IOException, ApiException {
        return sendPost("");
    }
    public String sendPost(String json) throws IOException, ApiException {
        SdpoLog.info("Request post to: " + url.toString());
        this.connection = (HttpsURLConnection)  this.url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", "1");
        connection.setRequestProperty("Authorization", "Bearer " + Sdpo.mainConfig.getString("token"));
        connection.setDoOutput(true);

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        SdpoLog.info("Response code: " + responseCode);
        InputStream inputStream;

        if (200 <= responseCode && responseCode <= 299) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        connection.disconnect();
        SdpoLog.info("Response: " + response.toString());

        if (200 > responseCode || responseCode > 299) {
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

            throw new ApiException(message);
        }

        return response.toString();
    }

    public URL getUrl() {
        return url;
    }
}
