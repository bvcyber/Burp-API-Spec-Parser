package com.bureauveritas.modelparser.control.file.loader;

import com.bureauveritas.modelparser.control.file.handler.openapi.OpenAPIFileHandler;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.io.File;

public class OpenAPIFileLoader extends AbstractModelFileLoaderChain<OpenAPI,OpenAPIFileHandler> {

    public OpenAPIFileLoader() {
        super(OpenAPIFileHandler::new);
    }

    @Override
    public OpenAPI loadModel(File file) throws Exception {
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setResolveFully(true);
        parseOptions.setResolveCombinators(true);
        SwaggerParseResult parser = new OpenAPIParser().readLocation(
            file.getAbsolutePath(),
            null,
            parseOptions
        );
        validateModel(parser);
        model = parser.getOpenAPI();
        if (model == null) {
            throw new Exception("OpenAPI file could not be parsed");
        }

        SwaggerParseResult unresolvedParser = new OpenAPIParser().readLocation(
            file.getAbsolutePath(),
            null,
            null
        );
        validateModel(unresolvedParser);
        OpenAPI unresolvedModel = unresolvedParser.getOpenAPI();
        if (unresolvedModel == null) {
            throw new Exception("OpenAPI file could not be parsed");
        }
        additionalProperties.put(OpenAPIFileHandler.UNRESOLVED_MODEL, unresolvedModel);

        return model;
    }

    private void validateModel(SwaggerParseResult parser) throws Exception {
        // SwaggerParser errors are outputted to getMessages()
        // TODO: is there a better way to check validation errors
        boolean hasMissingError = false;
        for (String message : parser.getMessages()) {
            System.err.println(message);
            if (message.endsWith("is missing")) {
                hasMissingError = true;
            }
        }
        if (hasMissingError) {
            throw new Exception("Missing required value(s)");
        }
    }
}
