package ru.nozdratenko.sdpo.Core.Network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class ParameterStringBuilder {
    public static String buildQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        return "?" + params.entrySet().stream()
            .filter(entry -> entry.getKey() != null && entry.getValue() != null)
            .map(entry -> encodeParam(entry.getKey()) + "=" + encodeParam(entry.getValue()))
            .collect(Collectors.joining("&"));
    }

    private static String encodeParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}
