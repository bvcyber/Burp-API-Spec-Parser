package com.bureauveritas.modelparser.model.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record McpResourcesSubscribeRequestModel(String method, Params params, String jsonrpc, Object id) implements iMcpRequest {
    @JsonCreator
    public McpResourcesSubscribeRequestModel(
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
    public record Params(String uri, Map<String, Object> meta) {
        @JsonCreator
        public Params(
            @JsonProperty("uri") String uri,
            @JsonProperty("_meta") Map<String, Object> meta
        ) {
            this.uri = uri;
            this.meta = meta;
        }
    }
}

