package com.bureauveritas.modelparser.control.file.handler.aws;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.bureauveritas.modelparser.BurpApi;
import com.bureauveritas.modelparser.control.BotocorePythonRPC;
import com.bureauveritas.modelparser.control.JMESPathUtils;
import com.bureauveritas.modelparser.control.file.handler.AbstractModelFileHandler;
import com.bureauveritas.modelparser.control.file.serializer.AbstractSerializer;
import com.bureauveritas.modelparser.control.file.serializer.SerializerType;
import com.bureauveritas.modelparser.model.proto.RequestSerializerOuterClass;
import software.amazon.awssdk.codegen.model.service.Member;
import software.amazon.awssdk.codegen.model.service.ServiceModel;
import software.amazon.awssdk.codegen.model.service.Shape;
import software.amazon.awssdk.utils.internal.CodegenNamingUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AWSServiceModelFileHandler extends AbstractModelFileHandler<ServiceModel> {
    public AWSServiceModelFileHandler(ServiceModel modelObject) {
        super(modelObject);
    }

    private final AbstractSerializer<HttpRequest> httpSerializer =
        new AbstractSerializer<HttpRequest>("HTTP Request", SerializerType.HTTP_REQUEST) {
            private HttpRequest serializeRequestImpl(String operationName) {
                BurpApi.getInstance().logging().logToOutput(
                    "Serializing AWS HTTP Request for operation \"" + operationName + "\"");
                try {
                    HttpRequest httpRequest = HttpRequest.httpRequest()
                        .withMethod(getOperationMethod(operationName))
                        .withPath(getOperationPath(operationName));
                    RequestSerializerOuterClass.SerializeRequestResponse response =
                        BotocorePythonRPC.getInstance().serializeRequest(getFileContent(), operationName);
                    if (response == null) {
                        throw new Exception("Check gRPC server connection - Failed to serialize request.");
                    }
                    for (Map.Entry<String, String> header : response.getHeadersMap().entrySet()) {
                        httpRequest = httpRequest.withAddedHeader(header.getKey(), header.getValue());
                    }
                    for (Map.Entry<String, String> query : response.getQueryStringMap().entrySet()) {
                        httpRequest = httpRequest.withAddedParameters(
                            HttpParameter.urlParameter(query.getKey(), query.getValue()));
                    }
                    return httpRequest.withBody(
                        ByteArray.byteArray(response.getBody().toByteArray()));
                } catch (Exception ex) {
                    BurpApi.getInstance().logging().logToError(ex);
                    return HttpRequest.httpRequest().withBody(String.format("An error occurred: \"%s\"", ex));
                }
            }

            @Override
            public HttpRequest serializeRequest(String operationName, boolean includeOptionalParameters) {
                HttpRequest httpRequest = serializeRequestImpl(operationName);
                if (!includeOptionalParameters) {
                    String inputShapeName = getOperationShapeName(operationName);

                    String updatedBody = getRequestBodyWithOnlyRequiredParameters(
                        httpRequest.bodyToString(), inputShapeName);
                    if (updatedBody != null) {
                        httpRequest = httpRequest.withBody(updatedBody);
                    }

                    Set<String> requiredQueryParams = getRequiredQueryParamsSet(inputShapeName);
                    for (ParsedHttpParameter queryParam : httpRequest.parameters(HttpParameterType.URL)) {
                        if (!requiredQueryParams.contains(queryParam.name())) {
                            httpRequest = httpRequest.withRemovedParameters(queryParam);
                        }
                    }
                }
                return httpRequest;
            }
        };

    private final AbstractSerializer<ByteArray> awscliSerializer =
        new AbstractSerializer<>("awscli Command", SerializerType.RAW) {
            @Override
            public ByteArray serializeRequest(String operationName, boolean includeOptionalParameters) {
                BurpApi.getInstance().logging().logToOutput(
                    "Serializing AWS CLI Command for operation \"" + operationName + "\"");
                String inputShapeName = getOperationShapeName(operationName);
                Shape modelShape = model.getShape(inputShapeName);
                List<String> requiredMembers = modelShape.getRequired();
                HashSet<String> requiredMemberNamesSet = requiredMembers == null ?
                    new HashSet<>() : new HashSet<>(requiredMembers);

                StringBuilder sb = new StringBuilder();
                sb.append("aws ")
                    .append(model.getMetadata().getEndpointPrefix()).append(" ")
                    .append(pascalCaseToKebabCase(operationName)).append(" ");
                for (Map.Entry<String, Member> entry : modelShape.getMembers().entrySet()) {
                    String memberName = entry.getKey();
                    if (!includeOptionalParameters && !requiredMemberNamesSet.contains(memberName)) {
                        continue;
                    }
                    Member member = entry.getValue();
                    String parameter = (member.getLocationName() == null || member.getLocationName().isEmpty()) ?
                        memberName : member.getLocationName();
                    sb.append("--")
                        .append(pascalCaseToKebabCase(parameter))
                        .append(" ").append("<value> ");
                }
                return ByteArray.byteArray(sb.toString().trim());
            }
        };

    @Override
    public List<String> getOperations() {
        return new ArrayList<>(model.getOperations().keySet());
    }

    @Override
    public List<String> getShapeNames() {
        return new ArrayList<>(model.getShapes().keySet());
    }

    @Override
    public String getServiceName() {
        return model.getMetadata().getEndpointPrefix();
    }

    @Override
    public String getModelType() {
        return "AWS";
    }

    @Override
    public String getOperationMethod(String operationName) {
        return model.getOperations().get(operationName).getHttp().getMethod();
    }

    @Override
    public String getOperationDefinition(String operationName) {
        return queryJMESPathOnFileContent(String.format("operations.%s", operationName));
    }

    @Override
    public String getOperationPath(String operationName) {
        return model.getOperations().get(operationName).getHttp().getRequestUri();
    }

    @Override
    public String getOperationShapeName(String operationName) {
        return model.getOperations().get(operationName).getInput().getShape();
    }

    @Override
    public String getShapeDefinition(String shapeName) {
        return queryJMESPathOnFileContent(String.format("shapes.%s", shapeName));
    }

    @Override
    public boolean updateSerializers(String operationName) {
        if (serializers.isEmpty()) {
            addSerializers(httpSerializer, awscliSerializer);
            return true;
        }
        return false;
    }

    @Override
    public ByteArray applyHost(String host, ByteArray byteArray, String operationName) {
        if (host == null || host.isBlank()) {
            return byteArray;
        }
        return byteArray.withAppended(" --endpoint-url " + host);
    }

    private static String pascalCaseToKebabCase(String s) {
        return String.join("-", CodegenNamingUtils.splitOnWordBoundaries(s)).toLowerCase();
    }

    private Set<String> getRequiredQueryParamsSet(String inputShapeName) {
        List<String> requiredMembers = model.getShape(inputShapeName).getRequired();
        if (requiredMembers == null) {
            return new HashSet<>();
        }
        return requiredMembers.stream()
            .filter(member -> {
                String location = model.getShape(inputShapeName).getMembers().get(member).getLocation();
                return location != null && location.equals("querystring");
            })
            .map(member -> model.getShape(inputShapeName).getMembers().get(member).getLocationName())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    private String getRequestBodyWithOnlyRequiredParameters(String requestBody, String shapeName) {
        // TODO: currently this only works with JSON request bodies i.e. type: "structure"
        try {
            if (requestBody.isEmpty()) {
                return null;
            }
            Shape modelShape = model.getShape(shapeName);
            if (!modelShape.getType().equals("structure")) {
                return null;
            }

            List<String> requiredParametersList = modelShape.getRequired();
            HashMap<String, String> requiredParameterAndNames;
            if (requiredParametersList != null && !requiredParametersList.isEmpty()) {
                requiredParameterAndNames = requiredParametersList.stream()
                    .filter(requiredParameter -> {
                        String memberLocation = modelShape.getMembers().get(requiredParameter).getLocation();
                        return memberLocation == null || memberLocation.isEmpty() ||
                            !(memberLocation.equals("uri") || memberLocation.equals("querystring"));
                    })
                    .collect(Collectors.toMap(
                        requiredParameter -> requiredParameter,
                        requiredParameter -> {
                            String locationName = modelShape.getMembers().get(requiredParameter).getLocationName();
                            // need to handle null or else it throws NPE
                            if (locationName == null) {
                                locationName = requiredParameter; // this is often the case for "ec2" protocol
                                // TODO: does this break other protocols
                            }
                            return locationName;
                        },
                        (existing, replacement) -> existing,
                        HashMap::new
                    ));
            }
            else {
                return "{}";
            }
            if (requiredParameterAndNames.isEmpty()) {
                return "{}";
            }

            // Extract only the required parameters in the request body JSON
            String jsonWithRequiredFields = JMESPathUtils.executeQuery(
                requestBody, buildJMESPathExpression(requiredParameterAndNames.values()));
            // Recurse for each parameter subfields, use JsonObject to rebuild
            for (String parameterName : requiredParameterAndNames.keySet()) {
                JsonObject jsonObject = JsonParser.parseString(jsonWithRequiredFields).getAsJsonObject();
                String updatedObjectString = getRequestBodyWithOnlyRequiredParameters(
                    JMESPathUtils.executeQuery(jsonWithRequiredFields, parameterName),
                    modelShape.getMembers().get(parameterName).getShape());
                if (updatedObjectString == null) {
                    continue;
                }
                jsonObject.add(parameterName, JsonParser.parseString(updatedObjectString));
                jsonWithRequiredFields = jsonObject.toString();
            }
            return jsonWithRequiredFields;
        }
        catch (Exception e) {
            BurpApi.getInstance().logging().logToError(e);
            return requestBody;
        }
    }

    private static String buildJMESPathExpression(Collection<String> parameterNames) {
        // Create a JMESPath expression to select the specified fields
        return "{" +
            parameterNames.stream()
                .map(n -> "%s: %s".formatted(n, n))
                .collect(Collectors.joining(", ")) +
            "}";
    }
}
