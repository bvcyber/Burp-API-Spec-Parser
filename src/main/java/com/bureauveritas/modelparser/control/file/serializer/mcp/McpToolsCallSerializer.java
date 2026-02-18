package com.bureauveritas.modelparser.control.file.serializer.mcp;

import burp.api.montoya.http.message.requests.HttpRequest;
import com.bureauveritas.modelparser.control.JsonSchemaExampleGenerator;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;
import com.bureauveritas.modelparser.model.mcp.McpRequestBuilder;
import com.bureauveritas.modelparser.model.mcp.McpServerType;
import com.bureauveritas.modelparser.model.mcp.McpToolRequestModel;
import io.modelcontextprotocol.spec.McpSchema.JsonSchema;
import tools.jackson.databind.JsonNode;

import java.util.*;

import static com.bureauveritas.modelparser.control.file.handler.mcp.McpUtils.*;

public class McpToolsCallSerializer extends AbstractMcpSerializer {
    private final String toolName;
    private final String pathWithQuery;
    private final JsonNode schemaNode;
    private final boolean isHttpStreaming;
    private final boolean withSessionId;


    public McpToolsCallSerializer(String pathWithQuery, JsonSchema inputSchema, String toolName, String operationName,
                                  McpServerType serverType, boolean withSessionId, Map<String, String> headers) {
        super("HTTP Request" + (withSessionId ? "" : " (no session ID)"), SerializerType.HTTP_REQUEST, operationName, headers);
        this.pathWithQuery = pathWithQuery;
        this.toolName = toolName;
        this.schemaNode = JSON_MAPPER.valueToTree(inputSchema);
        this.isHttpStreaming = McpServerType.HTTP.equals(serverType);
        this.withSessionId = withSessionId;
    }

    @Override
    public HttpRequest serializeRequest(String operationName, boolean includeOptionalParameters) {
        Map<String, Object> arguments = new HashMap<>();

        JsonNode schemaProperties = schemaNode.get("properties");
        JsonNode schemaRequired = schemaNode.get("required");

        Set<String> requiredFields = new HashSet<>();
        if (schemaRequired != null && schemaRequired.isArray()) {
            schemaRequired.forEach(r -> requiredFields.add(r.asString()));
        }

        if (schemaProperties != null) {
            schemaProperties.properties().forEach(entry -> {
                String fieldName = entry.getKey();
                boolean isRequired = requiredFields.contains(fieldName);

                if (!includeOptionalParameters && !isRequired) {
                    return;
                }

                arguments.put(
                    fieldName, JsonSchemaExampleGenerator.generate(fieldName, entry.getValue(), schemaNode));
            });
        }
        HttpRequest request = HttpRequest.httpRequest()
            .withMethod("POST")
            .withPath(pathWithQuery)
            .withHeader("Content-Type", "application/json")
            .withBody(JSON_MAPPER.writeValueAsString(
                McpRequestBuilder.toolRequest(1, McpRequestBuilder.TOOLS_CALL)
                    .params(McpToolRequestModel.Params.builder()
                        .name(toolName)
                        .arguments(arguments)
                        .build()
                    )
                    .build()
            ));

        if (isHttpStreaming) {
            request = applyHttpStreamingHeaders(request, withSessionId);
        }

        return applyHeaders(request);
    }
}
