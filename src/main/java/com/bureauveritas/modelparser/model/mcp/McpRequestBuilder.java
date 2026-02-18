package com.bureauveritas.modelparser.model.mcp;

public class McpRequestBuilder {

    public static final String ESTABLISH_SSE = "Establish SSE Stream";
    public static final String TOOL_PREFIX = "tool/";
    public static final String INITIALIZE = "initialize";
    public static final String PING = "ping";
    public static final String NOTIFICATIONS_INITIALIZED = "notifications/initialized";
    public static final String TOOLS_CALL = "tools/call";
    public static final String LOGGING_SET_LEVEL = "logging/setLevel";
    public static final String PROMPTS_LIST = "prompts/list";
    public static final String PROMPTS_GET = "prompts/get";
    public static final String RESOURCES_LIST = "resources/list";
    public static final String RESOURCES_READ = "resources/read";
    public static final String RESOURCES_SUBSCRIBE = "resources/subscribe";
    public static final String RESOURCES_UNSUBSCRIBE = "resources/unsubscribe";

    public static McpInitializeRequestModel.McpInitializeRequestModelBuilder initializeRequest(Object id) {
        return McpInitializeRequestModel.builder()
            .id(id)
            .jsonrpc("2.0")
            .method(INITIALIZE);
    }

    public static McpLoggingSetLevelRequestModel.McpLoggingSetLevelRequestModelBuilder loggingSetLevelRequest(Object id) {
        return McpLoggingSetLevelRequestModel.builder()
            .id(id)
            .jsonrpc("2.0")
            .method(LOGGING_SET_LEVEL);
    }

    public static McpToolRequestModel.McpToolRequestModelBuilder toolRequest(Object id, String method) {
        return McpToolRequestModel.builder()
            .id(id)
            .jsonrpc("2.0")
            .method(method);
    }

    public static McpUriRequestModel.McpUriRequestModelBuilder uriRequest(Object id, String method) {
        return McpUriRequestModel.builder()
            .id(id)
            .jsonrpc("2.0")
            .method(method);
    }

    public static McpListRequestModel.McpListRequestModelBuilder listRequest(Object id, String method) {
        return McpListRequestModel.builder()
            .id(id)
            .jsonrpc("2.0")
            .method(method);
    }
}
