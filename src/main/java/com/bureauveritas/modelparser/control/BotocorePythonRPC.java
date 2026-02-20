package com.bureauveritas.modelparser.control;

import com.bureauveritas.modelparser.BurpApi;
import io.grpc.*;
import com.bureauveritas.modelparser.model.proto.RequestSerializerOuterClass.SerializeRequestRequest;
import com.bureauveritas.modelparser.model.proto.RequestSerializerOuterClass.SerializeRequestResponse;
import com.bureauveritas.modelparser.model.proto.RequestSerializerGrpc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BotocorePythonRPC {
    private RequestSerializerGrpc.RequestSerializerBlockingV2Stub stub;
    private ManagedChannel channel;
    private ExecutorService executor;
    private static BotocorePythonRPC instance;
    private final int timeoutSeconds = 3;

    public static BotocorePythonRPC getInstance() {
        if (instance == null) {
            instance = new BotocorePythonRPC();
        }
        return instance;
    }

    private BotocorePythonRPC() {
        connect(50055);
    }

    public void connect(int p) {
        if (p <= 0 || p > 65535) {
            throw new IllegalArgumentException("Port number must be between 1 and 65535.");
        }

        BurpApi.getInstance().logging().logToOutput(
            String.format("Configuring gRPC stub on port %d...", p));

        cleanupResources();
        try {
            executor = Executors.newCachedThreadPool();
            channel = Grpc.newChannelBuilder(
                    String.format("localhost:%d", p),
                    InsecureChannelCredentials.create())
                .executor(executor)
                .build();
            stub = RequestSerializerGrpc.newBlockingV2Stub(channel);
            BurpApi.getInstance().logging().logToOutput("Stub created successfully");
        }
        catch (Exception e) {
            BurpApi.getInstance().logging().logToError(e);
            cleanupResources();
        }
    }

    private void cleanupResources() {
        if (channel != null) {
            BurpApi.getInstance().logging().logToOutput("Shutting down gRPC channel...");
            try {
                channel.shutdown();
                if (!channel.awaitTermination(3, TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            }
            catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
            channel = null;
        }

        if (executor != null) {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
            catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            executor = null;
        }

        stub = null;
    }


    public SerializeRequestResponse serializeRequest(String modelString, String operationName) {
        try {
            if (stub == null) {
                BurpApi.getInstance().logging().logToOutput("gRPC server not configured");
                return null;
            }
            SerializeRequestRequest request = SerializeRequestRequest.newBuilder()
                .setModelStr(modelString)
                .setOperationName(operationName)
                .build();
            BurpApi.getInstance().logging().logToOutput(
                String.format("Sending serialize request for operation %s...", operationName));
            SerializeRequestResponse response = stub
                .withDeadlineAfter(timeoutSeconds, TimeUnit.SECONDS)
                .serializeRequest(request);
            BurpApi.getInstance().logging().logToOutput("Returned from stub.serializeRequest");
            return response;
        } catch (Exception e) {
            BurpApi.getInstance().logging().logToError(e);
            throw new RuntimeException(e);
        }
    }

    public String healthCheck() {
        BurpApi.getInstance().logging().logToOutput("Testing connection to gRPC server...");
        try {
            String minimalJson = "{\"version\":\"2.0\",\"metadata\":{\"apiVersion\":\"2023-01-01\",\"protocol\":\"rest-json\",\"serviceId\":\"TestService\",\"signingName\":\"test\",\"signatureVersion\":\"v4\"},\"operations\":{\"GetItem\":{\"name\":\"GetItem\",\"http\":{\"method\":\"GET\",\"requestUri\":\"/items/{id}\"},\"input\":{\"shape\":\"GetItemRequest\"}}},\"shapes\":{\"GetItemRequest\":{\"type\":\"structure\",\"required\":[\"id\"],\"members\":{\"id\":{\"shape\":\"String\",\"location\":\"uri\",\"locationName\":\"id\"}}},\"String\":{\"type\":\"string\"}}}";
            SerializeRequestResponse response = serializeRequest(minimalJson, "GetItem");
            BurpApi.getInstance().logging().logToOutput("Got response");
            if (response != null &&
                response.getMethod().equals("GET") &&
                response.getUrlPath().equals("/items/") &&
                response.getBody().isEmpty()) {
                return "OK";
            }
            return "Incorrect response";
        }
        catch (Exception e) {
            BurpApi.getInstance().logging().logToError(e);
            return e.getMessage();
        }
    }
}
