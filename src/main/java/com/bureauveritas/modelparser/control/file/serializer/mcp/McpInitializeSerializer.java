package com.bureauveritas.modelparser.control.file.serializer.mcp;

import burp.api.montoya.http.message.requests.HttpRequest;
import com.bureauveritas.modelparser.control.file.handler.mcp.McpUtils;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;
import com.bureauveritas.modelparser.model.mcp.McpInitializeRequestModel;
import com.bureauveritas.modelparser.model.mcp.McpRequestBuilder;
import com.bureauveritas.modelparser.model.mcp.McpServerType;
import io.modelcontextprotocol.spec.ProtocolVersions;

import java.util.HashMap;
import java.util.Map;

import static com.bureauveritas.modelparser.control.file.handler.mcp.McpUtils.JSON_MAPPER;

public class McpInitializeSerializer extends AbstractMcpSerializer {
    private final String path;
    private final boolean withCapabilities;
    private final boolean isHttpStreaming;

    public McpInitializeSerializer(String path, String operationName, boolean withCapabilities, McpServerType serverType, Map<String, String> headers) {
        super("HTTP Request" + (withCapabilities ? " (with capabilities)" : " (no capabilities)"),
            SerializerType.HTTP_REQUEST,
            operationName, headers);
        this.path = path;
        this.withCapabilities = withCapabilities;
        this.isHttpStreaming = McpServerType.HTTP.equals(serverType);
    }

    @Override
    public HttpRequest serializeRequest(String operationName, boolean includeOptionalParameters) {
        try {
            HttpRequest request = HttpRequest.httpRequest()
                .withMethod("POST")
                .withPath(path)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_MAPPER.writeValueAsString(McpRequestBuilder.initializeRequest(1)
                    .params(McpInitializeRequestModel.Params.builder()
                        .protocolVersion(ProtocolVersions.MCP_2025_11_25)
                        .clientInfo(McpInitializeRequestModel.ClientInfo.builder()
                            .name("ExampleClient")
                            .version("1.0")
                            .build())
                        .capabilities(withCapabilities ?
                            McpInitializeRequestModel.Capabilities.builder()
                                .elicitation(new HashMap<>())
                                .sampling(new HashMap<>())
                                .roots(McpInitializeRequestModel.RootsCapability.builder().listChanged(true).build())
                                .build()
                            :
                            McpInitializeRequestModel.Capabilities.builder()
                                .build())
                        .build()
                    )
                    .build()));
            request = isHttpStreaming ?
                McpUtils.applyHttpStreamingHeaders(request, false) :
                request.withHeader("Accept", "application/json");
            return applyHeaders(request);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
