package com.bureauveritas.modelparser.control.file.handler.openapi;

import org.openapitools.codegen.CodegenParameter;

public class OpenAPISampleGenerator {
    private static final java.util.Random random = new java.util.Random();

    public static String getSampleValue(CodegenParameter param) {
//        BurpApi.getInstance().logging().logToOutput("getSampleValue for parameter: " + param.baseName);
//        BurpApi.getInstance().logging().logToOutput("  defaultValue: " + param.defaultValue);
//        BurpApi.getInstance().logging().logToOutput("  defaultValue == null: " + (param.defaultValue == null));
//        BurpApi.getInstance().logging().logToOutput("  defaultValue.equals(\"null\"): " + (param.defaultValue.equals("null")));
//        BurpApi.getInstance().logging().logToOutput("  example: " + param.example);
//        BurpApi.getInstance().logging().logToOutput("  examples: " + param.examples);
//        BurpApi.getInstance().logging().logToOutput("  paramName: " + param.paramName);
//        BurpApi.getInstance().logging().logToOutput("  isString: " + param.isString);
//        BurpApi.getInstance().logging().logToOutput("  isInteger: " + param.isInteger);
//        BurpApi.getInstance().logging().logToOutput("  isLong: " + param.isLong);
//        BurpApi.getInstance().logging().logToOutput("  isNumber: " + param.isNumber);
//        BurpApi.getInstance().logging().logToOutput("  isFloat: " + param.isFloat);
//        BurpApi.getInstance().logging().logToOutput("  isDouble: " + param.isDouble);
//        BurpApi.getInstance().logging().logToOutput("  isBoolean: " + param.isBoolean);
//        BurpApi.getInstance().logging().logToOutput("  isDate: " + param.isDate);
//        BurpApi.getInstance().logging().logToOutput("  isDateTime: " + param.isDateTime);
//        BurpApi.getInstance().logging().logToOutput("  isUuid: " + param.isUuid);
//        BurpApi.getInstance().logging().logToOutput("  isArray: " + param.isArray);
//        BurpApi.getInstance().logging().logToOutput("  isMap: " + param.isMap);

        if (param.defaultValue != null && !param.defaultValue.isEmpty() && !param.defaultValue.equals("null")) {
            return param.defaultValue;
        }

        // Note: example and examples can be inconsistent in the Codegen library
        if (param.example != null && !param.example.isEmpty()) {
            return param.example;
        }
        if (param.examples != null && !param.examples.isEmpty()) {
            // Get a random example
            return param.examples.entrySet().stream().toList()
                .get(random.nextInt(param.examples.size())).getValue().toString();
        }
        if (param.getContent() != null && !param.getContent().isEmpty()) {
            // Get a random content type example
            Object example = param.getContent().entrySet().stream().toList()
                .get(random.nextInt(param.getContent().size())).getValue().getExample();
            if (example != null && !example.toString().isEmpty()) {
                return example.toString();
            }
        }

        // If no default values, create a placeholder
        String paramName = param.paramName != null ? param.paramName : "value"; // Use parameter name as hint
        if (param.isString) {
            return "<string>";
        }
        if (param.isInteger || param.isLong) {
            return "0";
        }
        if (param.isNumber || param.isFloat || param.isDouble) {
            return "0.0";
        }
        if (param.isBoolean) {
            return "<boolean>";
        }
        if (param.isDate) {
            return "<date>";
        }
        if (param.isDateTime) {
            return "<datetime>";
        }
        if (param.isUuid) {
            return "<uuid>";
        }
        if (param.isArray) {
            return "[]";
        }
        if (param.isMap) {
            return "{}";
        }

        // Generic fallback
        return String.format("<%s>", paramName);
    }
}
