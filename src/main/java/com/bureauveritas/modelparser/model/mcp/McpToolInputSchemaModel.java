package com.bureauveritas.modelparser.model.mcp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;


/*
 * the JSON Schema spec
 */
@Getter
@Builder
public class McpToolInputSchemaModel {
    private final String type;
    private final Map<String, SchemaProperty> properties;
    private final List<String> required;
    private final SchemaProperty items; // if type is array at the top level

    @JsonCreator
    public McpToolInputSchemaModel(
        @JsonProperty("type") String type,
        @JsonProperty("properties") Map<String, SchemaProperty> properties,
        @JsonProperty("required") List<String> required,
        @JsonProperty("items") SchemaProperty items
    ) {
        this.type = type;
        this.properties = properties;
        this.required = required;
        this.items = items;
    }

    @Getter
    @Builder
    public static class SchemaProperty {
        // core type info
        private final String type;
        private final String description;

        // enum constraint - no type needed when present
        private final List<Object> enumValues;

        // default value - can be any JSON type
        private final Object defaultValue;

        // string constraints
        private final Integer minLength;
        private final Integer maxLength;
        private final String pattern; // regex

        // numeric constraints
        private final Number minimum;
        private final Number maximum;
        private final Number exclusiveMinimum;
        private final Number exclusiveMaximum;
        private final Number multipleOf;

        // array constraints
        private final SchemaProperty items; // type of each element
        private final Integer minItems;
        private final Integer maxItems;
        private final Boolean uniqueItems;

        // object constraints
        private final Map<String, SchemaProperty> properties; // nested object
        private final List<String> required;
        private final Boolean additionalProperties;

        // composition
        private final List<SchemaProperty> anyOf;
        private final List<SchemaProperty> oneOf;
        private final List<SchemaProperty> allOf;
        private final SchemaProperty not;

        // reference to another schema definition
        @JsonProperty("$ref")
        private final String ref;

        private final String title;           // short label, different from description
        private final List<Object> examples;  // example valid values
        private final Boolean readOnly;       // hint that field shouldn't be set by caller
        private final Boolean writeOnly;      // hint that field is input-only

        @JsonCreator
        public SchemaProperty(
            @JsonProperty("type") String type,
            @JsonProperty("description") String description,
            @JsonProperty("enum") List<Object> enumValues,
            @JsonProperty("default") Object defaultValue,
            @JsonProperty("minLength") Integer minLength,
            @JsonProperty("maxLength") Integer maxLength,
            @JsonProperty("pattern") String pattern,
            @JsonProperty("minimum") Number minimum,
            @JsonProperty("maximum") Number maximum,
            @JsonProperty("exclusiveMinimum") Number exclusiveMinimum,
            @JsonProperty("exclusiveMaximum") Number exclusiveMaximum,
            @JsonProperty("multipleOf") Number multipleOf,
            @JsonProperty("items") SchemaProperty items,
            @JsonProperty("minItems") Integer minItems,
            @JsonProperty("maxItems") Integer maxItems,
            @JsonProperty("uniqueItems") Boolean uniqueItems,
            @JsonProperty("properties") Map<String, SchemaProperty> properties,
            @JsonProperty("required") List<String> required,
            @JsonProperty("additionalProperties") Boolean additionalProperties,
            @JsonProperty("anyOf") List<SchemaProperty> anyOf,
            @JsonProperty("oneOf") List<SchemaProperty> oneOf,
            @JsonProperty("allOf") List<SchemaProperty> allOf,
            @JsonProperty("not") SchemaProperty not,
            @JsonProperty("$ref") String ref,
            @JsonProperty("title") String title,
            @JsonProperty("examples") List<Object> examples,
            @JsonProperty("readOnly") Boolean readOnly,
            @JsonProperty("writeOnly") Boolean writeOnly
        ) {
            this.type = type;
            this.description = description;
            this.enumValues = enumValues;
            this.defaultValue = defaultValue;
            this.minLength = minLength;
            this.maxLength = maxLength;
            this.pattern = pattern;
            this.minimum = minimum;
            this.maximum = maximum;
            this.exclusiveMinimum = exclusiveMinimum;
            this.exclusiveMaximum = exclusiveMaximum;
            this.multipleOf = multipleOf;
            this.items = items;
            this.minItems = minItems;
            this.maxItems = maxItems;
            this.uniqueItems = uniqueItems;
            this.properties = properties;
            this.required = required;
            this.additionalProperties = additionalProperties;
            this.anyOf = anyOf;
            this.oneOf = oneOf;
            this.allOf = allOf;
            this.not = not;
            this.ref = ref;
            this.title = title;
            this.examples = examples;
            this.readOnly = readOnly;
            this.writeOnly = writeOnly;
        }

        public boolean isRequired(String fieldName) {
            return required != null && required.contains(fieldName);
        }

        public boolean isEnum() {
            return enumValues != null && !enumValues.isEmpty();
        }

        public boolean isObject() {
            return type.equals("object") || properties != null;
        }

        public boolean isArray() {
            return type.equals("array");
        }
    }
}
