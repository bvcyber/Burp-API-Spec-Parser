package com.bureauveritas.modelparser.control.file.serializer.mcp;

import burp.api.montoya.core.ByteArray;
import com.bureauveritas.modelparser.control.file.serializer.AbstractSerializer;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;

public class McpSseEstablishCurlSerializer extends AbstractSerializer<ByteArray> {
    public McpSseEstablishCurlSerializer(String operationName) {
        super("Curl request", SerializerType.RAW, operationName);
    }

    @Override
    public ByteArray serializeRequest(String operationName, boolean includeOptionalParameters) {
        // Note: host/service is applied later in applyHost(), via getHosts()
        return ByteArray.byteArray("curl -N -H \"Accept: text/event-stream\" <URL>");
    }
}
