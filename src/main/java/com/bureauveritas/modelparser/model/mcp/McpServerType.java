package com.bureauveritas.modelparser.model.mcp;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum McpServerType {
    @JsonProperty("stdio") STDIO,
    @JsonProperty("sse") SSE,
    @JsonProperty("http") HTTP
}
