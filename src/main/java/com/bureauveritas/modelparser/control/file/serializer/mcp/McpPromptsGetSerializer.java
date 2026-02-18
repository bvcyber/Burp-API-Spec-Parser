package com.bureauveritas.modelparser.control.file.serializer.mcp;

import burp.api.montoya.http.message.requests.HttpRequest;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;
import com.bureauveritas.modelparser.model.mcp.McpRequestBuilder;
import com.bureauveritas.modelparser.model.mcp.McpServerType;
import com.bureauveritas.modelparser.model.mcp.McpToolRequestModel;

import java.util.HashMap;
import java.util.Map;

import static com.bureauveritas.modelparser.control.file.handler.mcp.McpUtils.*;

public class McpPromptsGetSerializer extends AbstractMcpSerializer {
    private final String pathWithQuery;
    private final boolean isHttpStreaming;
    private final boolean withSessionId;

    public McpPromptsGetSerializer(String pathWithQuery, String operationName, McpServerType serverType,
                                   boolean withSessionId, Map<String, String> headers) {
        super("HTTP Request" + (withSessionId ? "" : " (no session ID)"), SerializerType.HTTP_REQUEST, operationName, headers);
        this.pathWithQuery = pathWithQuery;
        this.isHttpStreaming = McpServerType.HTTP.equals(serverType);
        this.withSessionId = withSessionId;
    }

    @Override
    public HttpRequest serializeRequest(String operationName, boolean includeOptionalParameters) {
        Map<String, Object> arguments = null;
        if (includeOptionalParameters) {
            arguments = new HashMap<>();
            arguments.put("input", "value");
        }

        HttpRequest request = HttpRequest.httpRequest()
            .withMethod("POST")
            .withPath(pathWithQuery)
            .withHeader("Content-Type", "application/json")
            .withBody(JSON_MAPPER.writeValueAsString(
                McpRequestBuilder.toolRequest(1, McpRequestBuilder.PROMPTS_GET)
                    .params(McpToolRequestModel.Params.builder()
                        .name("<PROMPT_NAME>")
                        .arguments(arguments)
                        .build())
                    .build()
            ));

        if (isHttpStreaming) {
            request = applyHttpStreamingHeaders(request, withSessionId);
        }

        return applyHeaders(request);
    }
}

