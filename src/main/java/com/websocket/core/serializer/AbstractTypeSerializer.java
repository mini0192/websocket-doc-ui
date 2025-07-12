package com.websocket.core.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.websocket.exception.DocGenException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractTypeSerializer implements TypeSerializer {

    private final ObjectMapper objectMapper;

    @Override
    public String generateJson(Class<?> clazz) {
        try {
            Map<String, Object> map = generateMap(clazz, new IdentityHashMap<>());
            return convertMapToString(map);
        } catch (Exception e) {
            throw new DocGenException("Failed to generate documentation for class: " + clazz.getSimpleName(), e);
        }
    }

    protected AbstractTypeSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print
        this.objectMapper.findAndRegisterModules(); // For Java 8 date/time types
    }

    protected String convertMapToString(Map<String, Object> map) throws JsonProcessingException {
        return objectMapper.writeValueAsString(map);
    }

    private Map<String, Object> generateMap(Class<?> clazz, Map<Type, Boolean> visited) {
        if (visited.containsKey(clazz)) {
            return Collections.singletonMap("...recursive reference...", clazz.getSimpleName());
        }
        visited.put(clazz, true);

        Map<String, Object> map = new LinkedHashMap<>();
        for (Field field : getAllFields(clazz)) {
            if (field.isSynthetic()) { // Ignore compiler-generated fields
                continue;
            }
            field.setAccessible(true);
            String fieldName = field.getName();
            Type genericType = field.getGenericType();
            map.put(fieldName, getExampleValue(genericType, visited));
        }

        visited.remove(clazz);
        return map;
    }

    private Object getExampleValue(Type type, Map<Type, Boolean> visited) {
        if (type instanceof ParameterizedType pType) {
            Class<?> rawType = (Class<?>) pType.getRawType();
            Type[] typeArguments = pType.getActualTypeArguments();

            if (List.class.isAssignableFrom(rawType) || Set.class.isAssignableFrom(rawType)) {
                if (typeArguments.length > 0) {
                    return Collections.singletonList(getExampleValue(typeArguments[0], visited));
                } else {
                    return Collections.emptyList();
                }
            }
            if (Map.class.isAssignableFrom(rawType)) {
                if (typeArguments.length > 1) {
                    Object key = "key"; // Use a simple string literal for map keys
                    Object value = getExampleValue(typeArguments[1], visited);
                    return Collections.singletonMap(key, value);
                } else {
                    return Collections.emptyMap();
                }
            }
        }

        if (type instanceof Class<?> clazz) {
            if (clazz.isArray()) {
                return Collections.singletonList(getExampleValue(clazz.getComponentType(), visited));
            }
            if (isComplexType(clazz)) {
                return generateMap(clazz, visited);
            }
        }

        return getPrimitiveOrSimpleValue(type);
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private boolean isComplexType(Class<?> clazz) {
        return !clazz.isPrimitive()
                && !clazz.isEnum()
                && !clazz.getName().startsWith("java.");
    }

    protected abstract Object getPrimitiveOrSimpleValue(Type type);
}
