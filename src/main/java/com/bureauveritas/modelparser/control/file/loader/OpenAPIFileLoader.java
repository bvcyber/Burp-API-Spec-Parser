package com.bureauveritas.modelparser.control.file.loader;

import com.bureauveritas.modelparser.BurpApi;
import com.bureauveritas.modelparser.control.file.handler.openapi.OpenAPIFileHandler;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;

public class OpenAPIFileLoader extends AbstractModelFileLoaderChain<OpenAPI,OpenAPIFileHandler> {

    public OpenAPIFileLoader() {
        super(OpenAPIFileHandler::new);
    }

    @Override
    public OpenAPI loadModel(File file) throws Exception {
        JsonNode specNode = parseFileToJsonNode(file);
        validateRequiredFields(specNode);

        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setResolveFully(true);
        parseOptions.setResolveCombinators(true);

        SwaggerParseResult parser = new OpenAPIParser().readLocation(
            file.getAbsolutePath(), null, parseOptions
        );

        // swagger-parser can be very silent about issues in the spec. these messages can help identify problems with the input file
        for (String message : parser.getMessages()) {
            BurpApi.getInstance().logging().logToError("[parser] " + message);
        }

        model = parser.getOpenAPI();
        if (model == null) {
            throw new Exception("OpenAPI file could not be parsed");
        }

        SwaggerParseResult unresolvedParser = new OpenAPIParser().readLocation(
            file.getAbsolutePath(), null, null
        );

        OpenAPI unresolvedModel = unresolvedParser.getOpenAPI();
        if (unresolvedModel == null) {
            throw new Exception("OpenAPI file could not be parsed (unresolved)");
        }

        additionalProperties.put(OpenAPIFileHandler.UNRESOLVED_MODEL, unresolvedModel);
        return model;
    }

    /**
     * Validates that the spec has the minimum required top-level fields per the OAS spec.
     *
     * OAS 2.0 required: swagger (must be "2.0"), info, paths
     * OAS 3.x required: openapi (must start with "3."), info, paths
     *
     * This catches garbage JSON/YAML that swagger-parser silently accepts.
     */
    private void validateRequiredFields(JsonNode root) throws Exception {
        if (!root.isObject()) {
            throw new Exception("Spec must be a JSON/YAML object");
        }

        String version;

        if (root.has("swagger")) {
            // OAS 2.0 / Swagger
            version = root.get("swagger").asString();
            if (!version.startsWith("2.")) {
                throw new Exception("Unsupported Swagger version: " + version + " (expected 2.x)");
            }
        }
        else if (root.has("openapi")) {
            // OAS 3.x
            version = root.get("openapi").asString();
            if (!version.startsWith("3.")) {
                throw new Exception("Unsupported OpenAPI version: " + version + " (expected 3.x)");
            }
        }
        else {
            throw new Exception("Not a valid OpenAPI/Swagger spec: missing 'openapi' or 'swagger' field");
        }


        if (!root.has("paths") || root.get("paths").isNull()) {
            throw new Exception("Missing required field: 'paths'");
        }

        BurpApi.getInstance().logging().logToOutput("[loader] Detected spec version: " + version);
    }

    private JsonNode parseFileToJsonNode(File file) throws Exception {
        String name = file.getName().toLowerCase();
        try {
            if (name.endsWith(".yaml") || name.endsWith(".yml")) {
                return new ObjectMapper(new YAMLFactory()).readTree(file);
            }
            else {
                return new ObjectMapper().readTree(file);
            }
        } catch (Exception e) {
            throw new Exception("File is not valid JSON or YAML: " + e.getMessage());
        }
    }
}