package com.bureauveritas.modelparser.control.file.serializer.mcp;

import burp.api.montoya.http.message.requests.HttpRequest;
import com.bureauveritas.modelparser.control.file.serializer.AbstractSerializer;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;

import java.util.Map;

public abstract class AbstractMcpSerializer extends AbstractSerializer<HttpRequest> {
    protected final Map<String, String> headers;

    public AbstractMcpSerializer(String name, SerializerType serializerType, String operationName, Map<String, String> headers) {
        super(name, serializerType, operationName);
        this.headers = headers;
    }

    /**
     * Apply server headers to the HTTP request
     */
    protected HttpRequest applyHeaders(HttpRequest request) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request = request.withHeader(entry.getKey(), entry.getValue());
            }
        }
        return request;
    }
}

