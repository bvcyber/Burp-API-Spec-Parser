package com.bureauveritas.modelparser.control.file.serializer.mcp;

import com.bureauveritas.modelparser.control.file.serializer.AbstractSerializer;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class McpSerializerFactory {
    private final SerializerContext context;

    public List<AbstractSerializer<?>> createEstablishSseSerializers() {
        List<AbstractSerializer<?>> serializers = new ArrayList<>();
        String path = context.pathWithQuery().split("\\?")[0];
        serializers.add(new McpSseEstablishSerializer(path, context.operationName(), context.headers()));
        serializers.add(new McpSseEstablishCurlSerializer(context.operationName()));
        return serializers;
    }

    public List<AbstractSerializer<?>> createInitializeSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpInitializeSerializer(
                context.pathWithQuery(),
                context.operationName(),
                false,
                context.serverType(),
                context.headers()
            ),
            withSessionId -> new McpInitializeSerializer(
                context.pathWithQuery(),
                context.operationName(),
                true,
                context.serverType(),
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createPingSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpPingSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpPingSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createNotificationsInitializedSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpNotificationsInitializeSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpNotificationsInitializeSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createLoggingSetLevelSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpLoggingSetLevelSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpLoggingSetLevelSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createPromptsListSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpPromptsListSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpPromptsListSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createPromptsGetSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpPromptsGetSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpPromptsGetSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createResourcesListSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpResourcesListSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpResourcesListSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createResourcesReadSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpResourcesReadSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpResourcesReadSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createResourcesSubscribeSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpResourcesSubscribeSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpResourcesSubscribeSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createResourcesUnsubscribeSerializers() {
        return createSessionIdVariants(
            withSessionId -> new McpResourcesUnsubscribeSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                true,
                context.headers()
            ),
            withSessionId -> new McpResourcesUnsubscribeSerializer(
                context.pathWithQuery(),
                context.operationName(),
                context.serverType(),
                false,
                context.headers()
            )
        );
    }

    public List<AbstractSerializer<?>> createToolSerializers(McpSchema.Tool tool, String toolName) {
        List<AbstractSerializer<?>> serializers = new ArrayList<>();
        serializers.add(new McpToolsCallSerializer(
            context.pathWithQuery(),
            tool.inputSchema(),
            toolName,
            context.operationName(),
            context.serverType(),
            true,
            context.headers()
        ));
        serializers.add(new McpToolsCallSerializer(
            context.pathWithQuery(),
            tool.inputSchema(),
            toolName,
            context.operationName(),
            context.serverType(),
            false,
            context.headers()
        ));
        return serializers;
    }

    /**
     * Helper method to create serializer pairs with session ID variants for HTTP servers.
     * Only adds the secondary variant if the server is HTTP.
     */
    private List<AbstractSerializer<?>> createSessionIdVariants(
            Function<Boolean, AbstractSerializer<?>> withSessionId,
            Function<Boolean, AbstractSerializer<?>> withoutSessionId) {
        List<AbstractSerializer<?>> serializers = new ArrayList<>();
        serializers.add(withSessionId.apply(true));
        if (context.isHttp()) {
            serializers.add(withoutSessionId.apply(false));
        }
        return serializers;
    }
}

