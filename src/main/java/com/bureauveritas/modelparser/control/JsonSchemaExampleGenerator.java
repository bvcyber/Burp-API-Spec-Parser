package com.bureauveritas.modelparser.control;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.stream.StreamSupport;

import static com.bureauveritas.modelparser.control.file.handler.mcp.McpUtils.JSON_MAPPER;

public class JsonSchemaExampleGenerator {
    private static final Random RANDOM = new Random();

    public static Object generate(String fieldName, JsonNode fieldSchema, JsonNode rootSchema) {
        // resolve $ref first
        if (fieldSchema.has("$ref")) {
            JsonNode resolved = resolveRef(fieldSchema.get("$ref").asString(), rootSchema);
            if (resolved != null) {
                return generate(fieldName, resolved, rootSchema);
            }
            return fieldName;
        }

        // const
        if (fieldSchema.has("const")) {
            return unwrap(fieldSchema.get("const"));
        }

        // enum — get random
        if (fieldSchema.has("enum")) {
            JsonNode node = fieldSchema.get("enum");
            return unwrap(node.get(RANDOM.nextInt(node.size())));
        }

        // default
        if (fieldSchema.has("default")) {
            return unwrap(fieldSchema.get("default"));
        }

        // anyOf / oneOf — pick first non-null
        JsonNode combined = fieldSchema.has("anyOf") ? fieldSchema.get("anyOf") : fieldSchema.get("oneOf");
        if (combined != null && combined.isArray()) {
            for (JsonNode option : combined) {
                if (!isNullSchema(option)) {
                    return generate(fieldName, option, rootSchema);
                }
            }
        }

        // allOf — merge into one node
        if (fieldSchema.has("allOf")) {
            ObjectNode merged = JSON_MAPPER.createObjectNode();
            fieldSchema.get("allOf").forEach(s -> {
                if (s.isObject()) {
                    merged.setAll((ObjectNode) s);
                }
            });
            return generate(fieldName, merged, rootSchema);
        }

        // type
        JsonNode typeNode = fieldSchema.get("type");
        String type;

        if (typeNode == null) {
            return fieldName;
        }
        else if (typeNode.isArray()) {
            // union type e.g. ["string", "null"] — pick first non-null
            type = StreamSupport.stream(typeNode.spliterator(), false)
                .map(JsonNode::asString)
                .filter(t -> !t.equals("null"))
                .findFirst()
                .orElse("string");
        }
        else {
            type = typeNode.asString();
        }

        return switch (type) {
            case "string"  -> placeholderString(fieldName, fieldSchema);
            case "integer" -> placeholderInteger(fieldSchema);
            case "number"  -> placeholderNumber(fieldSchema);
            case "boolean" -> false;
            case "null"    -> null;
            case "array"   -> {
                JsonNode items = fieldSchema.get("items");
                yield items != null
                    ? List.of(generate(fieldName, items, rootSchema))
                    : List.of();
            }
            case "object"  -> {
                Map<String, Object> nested = new HashMap<>();
                JsonNode nestedProps = fieldSchema.get("properties");
                if (nestedProps != null) {
                    nestedProps.properties().forEach(e ->
                        nested.put(e.getKey(), generate(e.getKey(), e.getValue(), rootSchema))
                    );
                }
                yield nested;
            }
            default -> fieldName;
        };
    }

    private static String placeholderString(String fieldName, JsonNode fieldSchema) {
        JsonNode format = fieldSchema.get("format");
        if (format != null) {
            return switch (format.asString()) {
                case "date-time" -> "2024-01-01T00:00:00Z";
                case "date"      -> "2024-01-01";
                case "time"      -> "00:00:00";
                case "email"     -> "user@example.com";
                case "uri"       -> "https://example.com";
                case "uuid"      -> "01234567-89ab-cdef-fedc-ba9876543210";
                case "ipv4"      -> "127.0.0.1";
                case "ipv6"      -> "::1";
                case "hostname"  -> "example.com";
                default          -> "string";
            };
        }
        return "string";
    }

    private static int placeholderInteger(JsonNode fieldSchema) {
        if (fieldSchema.has("minimum")) {
            return fieldSchema.get("minimum").intValue();
        }
        if (fieldSchema.has("multipleOf")) {
            return fieldSchema.get("multipleOf").intValue();
        }
        return 0;
    }

    private static double placeholderNumber(JsonNode fieldSchema) {
        if (fieldSchema.has("minimum")) {
            return fieldSchema.get("minimum").doubleValue();
        }
        if (fieldSchema.has("multipleOf")) {
            return fieldSchema.get("multipleOf").doubleValue();
        }
        return 0.0;
    }

    // resolves "#/$defs/TypeName" or "#/definitions/TypeName"
    private static JsonNode resolveRef(String ref, JsonNode rootSchema) {
        if (!ref.startsWith("#/")) return null;
        String[] parts = ref.substring(2).split("/");
        JsonNode current = rootSchema;
        for (String part : parts) {
            current = current.get(part);
            if (current == null) return null;
        }
        return current;
    }

    // unwraps a JsonNode to a plain Java type
    private static Object unwrap(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return switch (node.getNodeType()) {
            case STRING -> node.asString();
            case BOOLEAN -> node.booleanValue();
            case NUMBER -> {
                if (node.isInt()) {
                    yield node.intValue();
                }
                if (node.isLong()) {
                    yield node.longValue();
                }
                if (node.isBigInteger()) {
                    yield node.bigIntegerValue();
                }
                if (node.isFloat()) {
                    yield node.floatValue();
                }
                if (node.isDouble()) {
                    yield node.doubleValue();
                }
                if (node.isBigDecimal()) {
                    yield node.decimalValue();
                }
                yield node.numberValue();
            }
            case ARRAY -> {
                List<Object> list = new ArrayList<>();
                node.forEach(n -> list.add(unwrap(n)));
                yield list;
            }
            case OBJECT -> {
                Map<String, Object> map = new HashMap<>();
                node.properties().forEach(e -> map.put(e.getKey(), unwrap(e.getValue())));
                yield map;
            }
            default -> node.asString();
        };
    }

    private static boolean isNullSchema(JsonNode schema) {
        JsonNode type = schema.get("type");
        if (type == null) {
            return false;
        }
        if (type.isArray()) {
            for (JsonNode t : type) {
                if (t.asString().equals("null")) {
                    return true;
                }
            }
            return false;
        }
        return type.asString().equals("null");
    }
}
