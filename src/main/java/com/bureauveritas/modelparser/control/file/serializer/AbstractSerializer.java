package com.bureauveritas.modelparser.control.file.serializer;

import lombok.Getter;

import java.util.Objects;

abstract public class AbstractSerializer<T> implements iSerializer<T> {
    private final String name;
    @Getter
    private final SerializerType serializerType;
    @Getter
    private final String operationName;

    public AbstractSerializer(String n, SerializerType sv) {
        name = n;
        serializerType = sv;
        operationName = null;
    }

    public AbstractSerializer(String n, SerializerType sv, String o) {
        name = n;
        serializerType = sv;
        operationName = o;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        // Check if same reference
        if (this == o) {
            return true;
        }

        // Check if null or different class
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        // Compare names
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, serializerType);
    }
}
