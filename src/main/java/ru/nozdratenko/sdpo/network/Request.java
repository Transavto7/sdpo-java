package ru.nozdratenko.sdpo.network;

import ru.nozdratenko.sdpo.Sdpo;

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

    public Request(String url) throws IOException {
        String baseURL = Sdpo.mainConfig.getString("url");

        if (!baseURL.endsWith("/") && !url.startsWith("/")) {
            baseURL += "/";
        }

        this.url = new URL(baseURL + url);
    }

    public String sendGet() throws IOException {
        return sendGet(new HashMap<>());
    }
    public String sendGet(Map<String, String> parameters) throws IOException {
        this.url = new URL(this.url.toString() + ParameterStringBuilder.getParamsString(parameters));

        this.connection = (HttpsURLConnection)  this.url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Length", "1");
        connection.setRequestProperty("Authorization", "Bearer " + Sdpo.mainConfig.getString("token"));
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        if (200 <= responseCode && responseCode <= 299) {
            inputStream = connection.getInputStream();
        } else {
            System.out.println(connection.getRequestMethod() + " " + responseCode);
            inputStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        connection.disconnect();
        return response.toString();
    }

    public String sendPost() throws IOException {
        return sendPost("");
    }
    public String sendPost(String json) throws IOException {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Length", "1");
        connection.setRequestProperty("Authorization", "Bearer " + Sdpo.mainConfig.getString("token"));
        connection.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(json);
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        if (200 <= responseCode && responseCode <= 299) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        connection.disconnect();
        return response.toString();
    }


    public URL getUrl() {
        return url;
    }
}
