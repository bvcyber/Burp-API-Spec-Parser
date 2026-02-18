package com.bureauveritas.modelparser.model.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record McpListRequestModel(String method, Params params, String jsonrpc, Object id) implements iMcpRequest {
    @JsonCreator
    public McpListRequestModel(
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
    public record Params(String cursor, Map<String, Object> meta) {
        @JsonCreator
        public Params(
            @JsonProperty("cursor") String cursor,
            @JsonProperty("_meta") Map<String, Object> meta
        ) {
            this.cursor = cursor;
            this.meta = meta;
        }
    }
}

