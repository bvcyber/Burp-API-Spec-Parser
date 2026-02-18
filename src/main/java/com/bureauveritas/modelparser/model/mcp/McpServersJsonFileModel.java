package com.bureauveritas.modelparser.model.mcp;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

import javax.validation.executable.ValidateOnExecution;
import java.util.*;

/**
 * Jackson model for the mcpServers json regularly used by mcp clients
 * There actually is no spec, but this is the format pretty much used by everyone
 */
@ValidateOnExecution
public class McpServersJsonFileModel {

    @Getter
    private final Map<String, McpServer> mcpServers;

    @JsonCreator
    public McpServersJsonFileModel(
        @JsonProperty(value = "mcpServers", required = true)
        Map<String, McpServer> mcpServers
    ) {
        this.mcpServers = mcpServers;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = McpServer.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = StdioMcpServer.class, name = "stdio"),
        @JsonSubTypes.Type(value = SseMcpServer.class, name = "sse"),
        @JsonSubTypes.Type(value = HttpMcpServer.class, name = "http"),
    })
    @Getter
    public static class McpServer {
        private final McpServerType type;
        private final Map<String, String> env;
        @JsonIgnore
        private final Map<String, Object> additionalProperties = new LinkedHashMap<>();

        @JsonCreator
        public McpServer(
            @JsonProperty(value = "type")
            McpServerType type,
            @JsonProperty("env")
            Map<String, String> env
        ) {
            this.type = type;
            this.env = env;
        }

        @JsonIgnore
        public McpServerType getType() {
            return type;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String key, Object value) {
            this.additionalProperties.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return additionalProperties;
        }
    }

    @Getter
    public static class StdioMcpServer extends McpServer {
        private final String command;
        private final List<String> args;

        @JsonCreator
        public StdioMcpServer(
            @JsonProperty(value = "command", required = true)
            String command,
            @JsonProperty(value = "args")
            List<String> args,
            @JsonProperty(value = "type", required = true)
            McpServerType type,
            @JsonProperty(value = "env")
            Map<String, String> env
        ) {
            super(type, env);
            this.command = command;
            this.args = args;
        }
    }

    @Getter
    public static class SseMcpServer extends McpServer {
        private final String url;
        private final Map<String, String> headers;

        @JsonCreator
        public SseMcpServer(
            @JsonProperty(value = "url", required = true)
            String url,
            @JsonProperty("headers")
            Map<String, String> headers,
            @JsonProperty(value = "type", required = true)
            McpServerType type,
            @JsonProperty(value = "env")
            Map<String, String> env
        ) {
            super(type, env);
            this.url = url;
            this.headers = headers;
        }
    }

    @Getter
    public static class HttpMcpServer extends McpServer {
        private final String url;
        private final Map<String, String> headers;

        @JsonCreator
        public HttpMcpServer(
            @JsonProperty(value = "url", required = true)
            String url,
            @JsonProperty("headers")
            Map<String, String> headers,
            @JsonProperty(value = "type", required = true)
            McpServerType type,
            @JsonProperty(value = "env")
            Map<String, String> env
        ) {
            super(type, env);
            this.url = url;
            this.headers = headers;
        }
    }
}
