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
                sb.append(mapToTypeScriptString((Map<String, Object>) value, indentLevel + 1));
            } else if (value instanceof List<?> list) {
                if (list.isEmpty()) {
                    sb.append("any[]");
                } else {
                    Object firstElement = list.get(0);
                    if (firstElement instanceof Map) {
                        sb.append("[");
                        sb.append(mapToTypeScriptString((Map<String, Object>) firstElement, indentLevel + 1));
                        sb.append("]");
                    } else {
                        String elementString = firstElement.toString();

                        String typePart = elementString;
                        String commentPart = "";

                        int commentIndex = elementString.indexOf("//");
                        if (commentIndex != -1) {
                            typePart = elementString.substring(0, commentIndex).trim();
                            commentPart = elementString.substring(commentIndex).trim();
                        }

                        // 타입 뒤에 [] 붙이고, 주석은 그대로 붙이기
                        sb.append(typePart).append("[]");
                        if (!commentPart.isEmpty()) {
                            sb.append(" ").append(commentPart);
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
            if (clazz == java.util.UUID.class) {
                return "string //UUID";
            }
            if (clazz == java.math.BigDecimal.class || clazz == java.math.BigInteger.class) {
                return "number";
            }
            if (clazz == java.util.Date.class || clazz == java.util.Calendar.class ||
                clazz == java.time.Instant.class || clazz == java.time.ZonedDateTime.class ||
                clazz == java.time.OffsetDateTime.class || clazz == java.time.Duration.class ||
                clazz == java.time.Period.class || clazz == java.time.LocalTime.class ||
                clazz == java.time.LocalDate.class || clazz == java.time.LocalDateTime.class) {
                return "string //Time";
            }
            if (clazz == java.net.URL.class || clazz == java.net.URI.class) {
                return "string //URL or URI";
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

