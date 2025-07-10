package com.websocket.core.classtojson;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class BasicClassToJson implements ClassToJson {

    @Override
    public String generateJson(Class<?> clazz) {
        try {
            Map<String, Object> map = generateMap(clazz, new LinkedHashMap<>());
            return mapToTypeScriptString(map, 0);
        } catch (Exception e) {
            return "// 예시 생성 실패: " + e.getMessage();
        }
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
            } else if (value instanceof String && ((String) value).startsWith("[") && ((String) value).endsWith("]")) {
                // Handle array types like [string], [number]
                sb.append(value);
            } else {
                sb.append(value);
            }
            firstEntry = false;
        }
        sb.append("\n").append(indent).append("}");
        return sb.toString();
    }

    private Map<String, Object> generateMap(Class<?> clazz, Map<Class<?>, Boolean> visited) {
        if (visited.containsKey(clazz)) {
            return Map.of("...recursive reference...", clazz.getSimpleName());
        }
        visited.put(clazz, true);

        Map<String, Object> map = new LinkedHashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals("this$0")) {
                continue;
            }
            Class<?> fieldType = field.getType();
            Type genericType = field.getGenericType();

            if (isPrimitiveOrWrapperOrString(fieldType)) {
                String tsType = getTypeScriptTypeName(fieldType);
                map.put(field.getName(), tsType);
            } else if (List.class.isAssignableFrom(fieldType) && genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                Type[] actualTypeArguments = pt.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    Class<?> listElementType = (Class<?>) actualTypeArguments[0];
                    String tsElementType = getTypeScriptTypeName(listElementType);
                    map.put(field.getName(), "[" + tsElementType + "]");
                } else {
                    map.put(field.getName(), "[]"); // Raw List
                }
            } else if (fieldType.isArray()) {
                Class<?> componentType = fieldType.getComponentType();
                String tsComponentType = getTypeScriptTypeName(componentType);
                map.put(field.getName(), "[" + tsComponentType + "]");
            } else if (fieldType.isEnum()) {
                map.put(field.getName(), getTypeScriptTypeName(fieldType));
            } else {
                map.put(field.getName(), generateMap(fieldType, visited));
            }
        }

        visited.remove(clazz);
        return map;
    }

    private String getTypeScriptTypeName(Class<?> clazz) {
        if (clazz == String.class) return "string";
        if (clazz == Integer.class || clazz == int.class ||
            clazz == Long.class || clazz == long.class ||
            clazz == Double.class || clazz == double.class ||
            clazz == Float.class || clazz == float.class ||
            clazz == Short.class || clazz == short.class ||
            clazz == Byte.class || clazz == byte.class) return "number";
        if (clazz == Boolean.class || clazz == boolean.class) return "boolean";
        if (clazz == Character.class || clazz == char.class) return "string";
        if (clazz.isEnum()) {
            StringBuilder enumValues = new StringBuilder();
            for (Object enumConstant : clazz.getEnumConstants()) {
                if (!enumValues.isEmpty()) {
                    enumValues.append(" | ");
                }
                enumValues.append("'").append(((Enum<?>) enumConstant).name()).append("'");
            }
            return enumValues.toString();
        }
        return clazz.getSimpleName(); // For other complex types
    }

    private boolean isPrimitiveOrWrapperOrString(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Boolean.class
                || Number.class.isAssignableFrom(clazz)
                || clazz == Character.class;
    }
}
