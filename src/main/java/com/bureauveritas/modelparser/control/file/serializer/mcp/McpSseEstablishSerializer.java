package com.bureauveritas.modelparser.control.file.serializer.mcp;

import burp.api.montoya.http.message.requests.HttpRequest;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;

import java.util.Map;

public class McpSseEstablishSerializer extends AbstractMcpSerializer {
    private final String path;

    public McpSseEstablishSerializer(String path, String operationName, Map<String, String> headers) {
        super("HTTP Request", SerializerType.HTTP_REQUEST, operationName, headers);
        this.path = path;
    }

    @Override
    public HttpRequest serializeRequest(String operationName, boolean includeOptionalParameters) {
        try {
            // Note: host/service is applied later in applyHost(), via getHosts()
            HttpRequest request = HttpRequest.httpRequest()
                .withMethod("GET")
                .withPath(path)
                .withHeader("Accept", "text/event-stream");
            return applyHeaders(request);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
