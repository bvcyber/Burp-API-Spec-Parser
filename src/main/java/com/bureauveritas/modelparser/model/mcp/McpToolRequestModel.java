package com.bureauveritas.modelparser.model.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

/**
 * {
 * "method": "tools/call",
 * "params": {
 * "name": "get_repositories",
 * "arguments": {
 * "projectPath": "C:\\Users\\philip\\path\\"
 * },
 * "_meta": {
 * "progressToken": 14
 * }
 * },
 * "jsonrpc": "2.0",
 * "id": 14
 * }
 *
 * @param id must be string or integer
 */

@Builder
public record McpToolRequestModel(String method, Params params, String jsonrpc, Object id) implements iMcpRequest {
    @JsonCreator
    public McpToolRequestModel(
        @JsonProperty("method") String method,
        @JsonProperty("params") Params params,
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") Object id
    ) {
        this.method = method;
        this.params = params;
        this.jsonrpc = jsonrpc;
        this.id = id; // must be string or integer
    }

    @Builder
    public record Params(String name, Map<String, Object> arguments, Map<String, Object> meta) {
        @JsonCreator
        public Params(
            @JsonProperty("name") String name,
            @JsonProperty("arguments") Map<String, Object> arguments,
            @JsonProperty("_meta") Map<String, Object> meta
        ) {
            this.name = name;
            this.arguments = arguments;
            this.meta = meta;
        }
    }
}
