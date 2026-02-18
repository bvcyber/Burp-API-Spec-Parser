package com.bureauveritas.modelparser.model.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;

import java.util.Map;

@Builder
public record McpLoggingSetLevelRequestModel(String method, Params params, String jsonrpc, Object id) implements iMcpRequest {
    @JsonCreator
    public McpLoggingSetLevelRequestModel(
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

    public enum LogLevel {
        DEBUG("debug"),
        INFO("info"),
        NOTICE("notice"),
        WARNING("warning"),
        ERROR("error"),
        CRITICAL("critical"),
        ALERT("alert"),
        EMERGENCY("emergency");

        private final String value;

        LogLevel(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static LogLevel fromValue(String value) {
            for (LogLevel level : values()) {
                if (level.value.equals(value)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Unknown log level: " + value);
        }
    }

    @Builder
    public record Params(LogLevel level, Map<String, Object> meta) {
        @JsonCreator
        public Params(
            @JsonProperty("level") LogLevel level,
            @JsonProperty("_meta") Map<String, Object> meta
        ) {
            this.level = level;
            this.meta = meta;
        }
    }
}
