package com.bureauveritas.modelparser.control.file.serializer;

public interface iSerializer<T> {
    T serializeRequest(String operationName, boolean includeOptionalParameters);
}
