package com.websocket.core;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassToJson {
    public String generateExampleJson(Class<?> clazz) {
        try {
            Map<String, Object> map = generateMap(clazz, new LinkedHashMap<>());
            return new GsonBuilder().setPrettyPrinting().create().toJson(map);
        } catch (Exception e) {
            return "// 예시 생성 실패: " + e.getMessage();
        }
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

            if (isPrimitiveOrWrapperOrString(fieldType)) {
                map.put(field.getName(), fieldType.getSimpleName());
            } else {
                map.put(field.getName(), generateMap(fieldType, visited));
            }
        }

        visited.remove(clazz);
        return map;
    }

    private boolean isPrimitiveOrWrapperOrString(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Boolean.class
                || Number.class.isAssignableFrom(clazz)
                || clazz == Character.class;
    }
}
