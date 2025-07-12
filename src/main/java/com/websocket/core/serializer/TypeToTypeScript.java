package com.websocket.core.serializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeToTypeScript extends AbstractTypeSerializer {

    @Override
    protected String convertMapToString(Map<String, Object> map) {
        return mapToTypeScriptString(map, 0);
    }

    private String mapToTypeScriptString(Map<String, Object> map, int indentLevel) {
        StringBuilder sb = new StringBuilder();
        String indent = "    ".repeat(indentLevel);
        String nextIndent = "    ".repeat(indentLevel + 1);

        sb.append("{\n");
        boolean firstEntry = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!firstEntry) {
                sb.append(",\n");
            }
            sb.append(nextIndent).append(entry.getKey()).append(": ");

            Object value = entry.getValue();

            if (value instanceof Map) {
                // Nested object: recursively call this method.
                sb.append(mapToTypeScriptString((Map<String, Object>) value, indentLevel + 1));
            } else if (value instanceof List<?> list) {
                // List type
                if (list.isEmpty()) {
                    sb.append("any[]");
                } else {
                    Object firstElement = list.get(0);
                    if (firstElement instanceof Map) {
                        // This reproduces the original format for a list of objects: [{...}]
                        sb.append("[");
                        sb.append(mapToTypeScriptString((Map<String, Object>) firstElement, indentLevel + 1));
                        sb.append("]");
                    } else {
                        // This is for a list of primitives, e.g., string[]
                        String elementString = firstElement.toString();
                        // Check if it's an enum union type string (e.g., 'A' | 'B')
                        if (elementString.matches("'.*'(\s*\\|\s*'.*')*")) {
                            // Transform 'A' | 'B' into ('A', 'B')
                            String tupleString = "(" + elementString.replace(" | ", ", ") + ")";
                            sb.append(tupleString).append("[]");
                        } else {
                            sb.append(elementString).append("[]");
                        }
                    }
                }
            } else {
                // Primitive type
                sb.append(value);
            }
            firstEntry = false;
        }
        sb.append("\n").append(indent).append("}");
        return sb.toString();
    }

    @Override
    protected Object getPrimitiveOrSimpleValue(Type type) {
        if (type instanceof Class<?> clazz) {
            if (clazz == String.class || clazz == Character.class || clazz == char.class) {
                return "string";
            }
            if (clazz.getName().equals("java.util.UUID")) {
                return "string //UUID";
            }
            if (clazz.getName().equals("java.time.LocalDateTime")) {
                return "string //LocalDateTime";
            }
            if (clazz.getName().equals("java.time.LocalDate")) {
                return "string //LocalDate";
            }
            if (clazz.getName().equals("java.time.LocalTime")) {
                return "string //LocalTime";
            }
            if (clazz == java.math.BigDecimal.class || clazz == java.math.BigInteger.class) {
                return "number";
            }
            if (clazz == java.util.Date.class || clazz == java.util.Calendar.class ||
                clazz == java.time.Instant.class || clazz == java.time.ZonedDateTime.class ||
                clazz == java.time.OffsetDateTime.class || clazz == java.time.Duration.class ||
                clazz == java.time.Period.class) {
                return "string"; // Represented as string in TypeScript
            }
            if (clazz == java.net.URL.class || clazz == java.net.URI.class) {
                return "string";
            }
            if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive() && clazz != boolean.class) {
                return "number";
            }
            if (clazz == Boolean.class || clazz == boolean.class) {
                return "boolean";
            }
            if (clazz.isEnum()) {
                return Arrays.stream(clazz.getEnumConstants())
                        .map(e -> "'" + ((Enum<?>) e).name() + "'")
                        .collect(Collectors.joining(" | "));
            }
        }
        return "any"; // Default for unknown simple types
    }
}

