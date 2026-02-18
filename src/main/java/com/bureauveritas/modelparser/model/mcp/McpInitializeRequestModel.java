package com.bureauveritas.modelparser.model.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

@Builder
public record McpInitializeRequestModel(String method, Params params, String jsonrpc, Object id) implements iMcpRequest {

    @JsonCreator
    public McpInitializeRequestModel(
        @JsonProperty("method") String method,
        @JsonProperty("params") Params params,
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") Object id
    ) {
        this.method = method;
        this.params = params;
        this.jsonrpc = jsonrpc;
        this.id = id;
    }

    @Builder
    public record Params(
        String protocolVersion,
        ClientInfo clientInfo,
        Capabilities capabilities,
        Map<String, Object> meta
    ) {
        @JsonCreator
        public Params(
            @JsonProperty("protocolVersion") String protocolVersion,
            @JsonProperty("clientInfo") ClientInfo clientInfo,
            @JsonProperty("capabilities") Capabilities capabilities,
            @JsonProperty("_meta") Map<String, Object> meta
        ) {
            this.protocolVersion = protocolVersion;
            this.clientInfo = clientInfo;
            this.capabilities = capabilities;
            this.meta = meta;
        }
    }

    @Builder
    public record ClientInfo(
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "version", required = true) String version,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("websiteUrl") String websiteUrl
    ) {}

    @Builder
    public record Capabilities(
        @JsonProperty("sampling") Map<String, Object> sampling,
        @JsonProperty("elicitation") Map<String, Object> elicitation,
        @JsonProperty("roots") RootsCapability roots
    ) {}

    @Builder
    public record RootsCapability(
        @JsonProperty("listChanged") Boolean listChanged
    ) {}
}
