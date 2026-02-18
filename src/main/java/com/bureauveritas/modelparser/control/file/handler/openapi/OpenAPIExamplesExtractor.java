package com.bureauveritas.modelparser.control.file.handler.openapi;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;

import java.util.*;

import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.openapitools.codegen.utils.ModelUtils;

public class OpenAPIExamplesExtractor {

    private final OpenAPI openAPI;
    private final OpenAPI unresolvedOpenAPI;

    public OpenAPIExamplesExtractor(String specPath) {
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setResolveFully(true);
        parseOptions.setResolveCombinators(true);
        SwaggerParseResult parser = new OpenAPIParser().readLocation(
            specPath,
            null,
            parseOptions
        );
        openAPI = parser.getOpenAPI();
        SwaggerParseResult unresolvedParser = new OpenAPIParser().readLocation(
            specPath,
            null,
            null
        );
        unresolvedOpenAPI = unresolvedParser.getOpenAPI();
    }

    public OpenAPIExamplesExtractor(OpenAPI model, OpenAPI unresolvedModel) {
        openAPI = model;
        unresolvedOpenAPI = unresolvedModel;
    }

    public Map<String,String> getParameterExamples(String path, String method, String parameter, String in) {
        Map<String, String> examples = new LinkedHashMap<>();

        Operation operation = getOperation(path, method);
        if (operation == null || operation.getParameters() == null) {
            return examples;
        }

        operation.getParameters().stream()
            .filter(param -> param.getName().equals(parameter) && isParamIn(param, in))
            .findAny()
            .ifPresent(param -> {
                // Get examples from content if available
                if (param.getContent() != null) {
                    param.getContent().forEach((contentType, mediaType) -> {
                        // The "example" (singular) field is mutually exclusive with the "examples" field
                        // https://swagger.io/specification/#working-with-examples
                        if (mediaType.getExamples() != null) {
                            mediaType.getExamples().forEach((exampleName, example) -> {
                                String exampleValue = OpenAPIUtils.convertExampleToString(
                                    example,
                                    contentType,
                                    OpenAPIUtils.getRefFromRequestBody(unresolvedOpenAPI, path, method, contentType)
                                );
                                examples.put(exampleName, exampleValue);
                            });
                            if (mediaType.getExample() != null) {
                                String exampleValue = OpenAPIUtils.convertObjectToStringByContentType(
                                    mediaType.getExample(),
                                    contentType,
                                    OpenAPIUtils.getRefFromRequestBody(unresolvedOpenAPI, path, method, contentType)
                                );
                                examples.put(String.format("%s_default", contentType), exampleValue);
                            }
                        }
                    });
                }
                // Get examples from parameter level if available
                if (param.getExamples() != null) {
                    param.getExamples().forEach((exampleName, example) -> {
                        String exampleValue = OpenAPIUtils.convertExampleToString(
                            example,
                            null,
                            getUnresolvedOperation(path, method).getParameters().stream()
                                .filter(p -> p.getName() != null && p.getName().equals(parameter) && isParamIn(p, in))
                                .findAny()
                                .map(p -> p.getSchema() != null ? p.getSchema().get$ref() : null)
                                .orElse(null)
                        );
                        examples.put(exampleName, exampleValue);
                    });
                }
                // Get example field from parameter level if available
                if (param.getExample() != null) {
                    String exampleValue = OpenAPIUtils.convertObjectToStringByContentType(
                        param.getExample(),
                        null,
                        getUnresolvedOperation(path, method).getParameters().stream()
                            .filter(p -> p.getName() != null && p.getName().equals(parameter) && isParamIn(p, in))
                            .findAny()
                            .map(p -> p.getSchema() != null ? p.getSchema().get$ref() : null)
                            .orElse(null)
                    );
                    examples.put("default", exampleValue);
                }
            });

        return examples;
    }

    private boolean isParamIn(Parameter param, String in) {
        if (in == null || in.isEmpty()) {
            return true;
        }
        return param.getIn().equals(in);
    }

    public String getRandomParameterExample(
        String path, String method, String parameter, String in
    ) {
        Map<String, String> examples = getParameterExamples(path, method, parameter, in);
        if (!examples.isEmpty()) {
            List<String> exampleValues = new ArrayList<>(examples.values());
            return exampleValues.get(new Random().nextInt(exampleValues.size()));
        }
        return "";
    }

    public Map<String, String> getRequestBodyExamples(String path, String method, String contentType) {
        return getRequestBodyExamples(path, method, contentType, false);
    }

    /**
     * Get all request body examples for a specific operation and content type; {exampleName: example}
     */
    public Map<String, String> getRequestBodyExamples(String path, String method, String contentType, boolean isFormData) {
        Map<String, String> examples = new LinkedHashMap<>();

        Operation operation = getOperation(path, method);

        // If operation has no request body, nothing to do
        if (operation == null || operation.getRequestBody() == null) {
            return examples;
        }

        RequestBody requestBody = operation.getRequestBody();
        Content content = requestBody.getContent();

        if (content == null || !content.containsKey(contentType)) {
            return examples;
        }

        MediaType mediaType = content.get(contentType);

        // Get examples from the examples map
        if (mediaType.getExamples() != null) {
            mediaType.getExamples()
                .forEach((exampleName, example) -> {
                String exampleValue = OpenAPIUtils.convertExampleToString(
                    resolveExampleRefs(example), // BUG: manually resolve as parser sometimes doesn't resolve refs on Example
                    contentType,
                    OpenAPIUtils.getRefFromRequestBody(unresolvedOpenAPI, path, method, contentType),
                    isFormData
                );
                examples.put(exampleName, exampleValue);
            });
        }
        // Get example from the single example field
        if (mediaType.getExample() != null) {
            String exampleValue = OpenAPIUtils.convertObjectToStringByContentType(
                mediaType.getExample(),
                contentType,
                OpenAPIUtils.getRefFromRequestBody(unresolvedOpenAPI, path, method, contentType),
                isFormData
            );
            String keyName = "default example - %s".formatted(contentType);
            for (int i=2; examples.containsKey(keyName); i++) {
                keyName = "default example%d - %s".formatted(i, contentType); // Avoid overwriting
            }
            examples.put(keyName, exampleValue);
        }

        return examples;
    }

    /**
     * Get all request body examples across all content types
     */
    public Map<String, Map<String, String>> getAllRequestBodyExamples(String path, String method) {
        Map<String, Map<String, String>> allExamples = new LinkedHashMap<>();

        Operation operation = getOperation(path, method);
        if (operation == null || operation.getRequestBody() == null) {
            return allExamples;
        }

        RequestBody requestBody = operation.getRequestBody();
        Content content = requestBody.getContent();

        if (content == null) {
            return allExamples;
        }

        // Iterate through all content types
        content.forEach((contentType, mediaType) -> {
            Map<String, String> examples = new LinkedHashMap<>();

            // Get examples from the examples map
            if (mediaType.getExamples() != null) {
                mediaType.getExamples().forEach((exampleName, example) -> {
                    String exampleValue = OpenAPIUtils.convertExampleToString(
                        example,
                        contentType,
                        OpenAPIUtils.getRefFromRequestBody(unresolvedOpenAPI, path, method, contentType)
                    );
                    examples.put(exampleName, exampleValue);
                });
            }
            // Fallback to single example field
            else if (mediaType.getExample() != null) {
                String exampleValue = OpenAPIUtils.convertObjectToStringByContentType(
                    mediaType.getExample(),
                    contentType,
                    OpenAPIUtils.getRefFromRequestBody(unresolvedOpenAPI, path, method, contentType)
                );
                examples.put("default", exampleValue);
            }

            if (!examples.isEmpty()) {
                allExamples.put(contentType, examples);
            }
        });

        return allExamples;
    }

    /**
     * Get a random example for a specific content type
     */
    public String getRandomRequestBodyExample(String path, String method, String contentType) {
        Map<String, String> examples = getRequestBodyExamples(path, method, contentType);

        if (examples.isEmpty()) {
            return "";
        }

        List<String> exampleValues = new ArrayList<>(examples.values());
        return exampleValues.get(new Random().nextInt(exampleValues.size()));
    }

    /**
     * Get a specific named example
     */
    public String getRequestBodyExampleByName(String path, String method, String contentType, String exampleName) {
        Map<String, String> examples = getRequestBodyExamples(path, method, contentType);
        return examples.getOrDefault(exampleName, "");
    }

    /**
     * Generate body content - picks the first available example or a random one
     */
    public String generateBodyContent(String path, String method, String contentType, boolean random) {
        Map<String, String> examples = getRequestBodyExamples(path, method, contentType);

        if (examples.isEmpty()) {
            return "";
        }

        if (random) {
            return getRandomRequestBodyExample(path, method, contentType);
        }
        else {
            // Return first example
            return examples.values().iterator().next();
        }
    }

    private Example resolveExampleRefs(Example example) {
        if (example.get$ref() == null) {
            return example;
        }
        return resolveExampleRefs(
            openAPI.getComponents().getExamples()
                .get(ModelUtils.getSimpleRef(example.get$ref()))
        );
    }

    private Operation getOperation(String path, String method) {
        return OpenAPIUtils.getOperation(path, method, openAPI);
    }

    private Operation getUnresolvedOperation(String path, String method) {
        return OpenAPIUtils.getOperation(path, method, unresolvedOpenAPI);
    }

    /**
     * Print all examples for debugging
     */
    public void printAllExamples(String path, String method) {
        System.out.println("=== Examples for " + method.toUpperCase() + " " + path + " ===\n");

        Map<String, Map<String, String>> allExamples = getAllRequestBodyExamples(path, method);

        allExamples.forEach((contentType, examples) -> {
            System.out.println("Content-Type: " + contentType);
            examples.forEach((exampleName, exampleValue) -> {
                System.out.println("  Example: " + exampleName);
                System.out.println("  Value: " + exampleValue);
                System.out.println();
            });
        });
    }
}
