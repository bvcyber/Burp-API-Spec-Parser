package com.bureauveritas.modelparser.control.file.serializer.mcp;

import com.bureauveritas.modelparser.model.mcp.McpServerType;

import java.util.Map;

/**
 * Context object containing all parameters needed to create serializers.
 * This eliminates the need to pass many parameters to each serializer constructor.
 */
public record SerializerContext(
    String pathWithQuery,
    String operationName,
    McpServerType serverType,
    Map<String, String> headers
) {
    /**
     * Check if the server is HTTP (as opposed to SSE)
     */
    public boolean isHttp() {
        return McpServerType.HTTP == serverType;
    }
}

