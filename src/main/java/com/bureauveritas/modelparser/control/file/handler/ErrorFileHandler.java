package com.bureauveritas.modelparser.control.file.handler;

import burp.api.montoya.core.ByteArray;
import com.bureauveritas.modelparser.control.file.serializer.AbstractSerializer;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;

import java.util.List;

public class ErrorFileHandler extends AbstractModelFileHandler<Object> {

    public ErrorFileHandler(Object model) {
        super(model);
        addSerializers(errorSerializer);
    }

    private final AbstractSerializer<ByteArray> errorSerializer = new AbstractSerializer<>(
        "Error Occurred",
        SerializerType.RAW
    ) {
        @Override
        public ByteArray serializeRequest(String operationName, boolean includeOptionalParameters) {
            return ByteArray.byteArray(
                "Error - no model loaded. Could not identify model type or encountered an exception while parsing.");
        }
    };

    @Override
    public List<String> getOperations() {
        return List.of("-");
    }

    @Override
    public String getModelType() {
        return "Could not load";
    }
}
