package com.websocket.core.serializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.UUID;

public class TypeToJson extends AbstractTypeSerializer {

    @Override
    protected Object getPrimitiveOrSimpleValue(Type type) {
        if (type instanceof Class<?> clazz) {
            if (clazz == String.class) {
                return "string";
            }
            if (clazz == Character.class || clazz == char.class) {
                return 'a';
            }
            if (Number.class.isAssignableFrom(clazz) || (clazz.isPrimitive() && clazz != boolean.class && clazz != char.class)) {
                return 0;
            }
            if (clazz == Boolean.class || clazz == boolean.class) {
                return false;
            }
            if (clazz == UUID.class) {
                return UUID.randomUUID().toString();
            }
            if (clazz == LocalDateTime.class) {
                return LocalDateTime.now().toString();
            }
            if (clazz == LocalDate.class) {
                return LocalDate.now().toString();
            }
            if (clazz == LocalTime.class) {
                return LocalTime.now().toString();
            }
            if (clazz == java.math.BigDecimal.class || clazz == java.math.BigInteger.class) {
                return 0; // Example number
            }
            if (clazz == java.util.Date.class) {
                return new java.util.Date().toInstant().toString(); // ISO 8601 string
            }
            if (clazz == java.util.Calendar.class) {
                return java.util.Calendar.getInstance().toInstant().toString(); // ISO 8601 string
            }
            if (clazz == java.time.Instant.class) {
                return java.time.Instant.now().toString();
            }
            if (clazz == java.time.ZonedDateTime.class) {
                return java.time.ZonedDateTime.now().toString();
            }
            if (clazz == java.time.OffsetDateTime.class) {
                return java.time.OffsetDateTime.now().toString();
            }
            if (clazz == java.time.Duration.class) {
                return java.time.Duration.ofHours(1).toString(); // Example duration
            }
            if (clazz == java.time.Period.class) {
                return java.time.Period.ofYears(1).toString(); // Example period
            }
            if (clazz == java.net.URL.class) {
                return "http://example.com";
            }
            if (clazz == java.net.URI.class) {
                return "/example/path";
            }
            if (clazz.isEnum()) {
                return Arrays.stream(clazz.getEnumConstants()).findFirst().map(Object::toString).orElse(null);
            }
        }
        return null; // Should not happen for known simple types
    }
}
