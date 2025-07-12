package com.websocket.core.serializer;

public interface TypeSerializer {
    String generateJson(Class<?> clazz);
}
