package com.bureauveritas.modelparser.control;

import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;

import java.util.List;
import java.util.Map;

public class HttpRequestUtils {

    public static HttpRequest addHttpHeaders(HttpRequest httpRequest, List<HttpHeader> headers) {
        if (headers.isEmpty()) {
            return httpRequest;
        }
        return httpRequest.withAddedHeaders(headers);
    }

    public static HttpRequest replacePlaceholderPathVariables(HttpRequest httpRequest, Map<String, String> variables) {
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            httpRequest = httpRequest
                    .withPath(httpRequest.path().replaceAll(
                            String.format("\\{%s\\}", variable.getKey()),
                            variable.getValue()));
        }
        return httpRequest;
    }
}
