package com.bureauveritas.modelparser.control.file.handler;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.bureauveritas.modelparser.control.file.serializer.AbstractSerializer;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;

import javax.swing.table.DefaultTableCellRenderer;
import java.util.List;

public interface iModelFileHandler {
    List<String> getAuthOptions(String operationName);
    List<String> getOperations();
    List<String> getShapeNames();
    List<AbstractSerializer<?>> getSerializers();
    SerializerType getSerializerType(String serializerName);
    String getServiceName();
    String getModelType();
    String getOperationMethod(String operationName);
    String getOperationDefinition(String operationName);
    String getOperationPath(String operationName);
    String getOperationShapeName(String operationName);
    String getShapeDefinition(String shapeName);
    Object serializeOperation(String operationName, AbstractSerializer<?> serializer, boolean includeOptionalParameters);
    boolean updateSerializers(String operationName);
    List<String> getHosts(String operationName);
    HttpRequest applyAuth(String auth, HttpRequest httpRequest);
    HttpRequest applyHost(String host, HttpRequest httpRequest);
    ByteArray applyHost(String host, ByteArray byteArray, String operationName);
    DefaultTableCellRenderer getOperationsTableCellRenderer();
    String getFormattedOperationName(String operationName);
}
