package com.websocket.core.serializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TypeToTypeScriptTest {

    private final TypeToTypeScript typeToTypeScript = new TypeToTypeScript();

    // Test DTOs (same as TypeToJsonTest for consistency)
    static class SimpleDto {
        public String name;
        public int age;
        public boolean active;
    }

    static class SimpleDto2 {
        public List<String> name;
        public List<UUID> uuid;
        public List<URL> url;
        public List<Date> date;
    }

    static class ComplexDto {
        public String id;
        public SimpleDto details;
        public List<String> tags;
        public Map<String, Integer> scores;
    }

    static class NestedListDto {
        public List<SimpleDto> items;
    }

    static class EnumDto {
        public MyEnum status;
    }

    enum MyEnum {
        ACTIVE, INACTIVE, PENDING
    }

    static class RecursiveDto {
        public String name;
        public RecursiveDto selfRef;
    }

    static class DateDto {
        public LocalDateTime createdAt;
        public LocalDate eventDate;
        public LocalTime eventTime;
        public UUID uuid;
    }

    static class PrimitiveArrayDto {
        public int[] numbers;
        public String[] names;
    }

    static class ObjectArrayDto {
        public SimpleDto[] simpleDto;
    }

    @Test
    @DisplayName("Should convert a simple DTO to TypeScript")
    void testSimpleDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(SimpleDto.class);
        assertNotNull(ts);
        assertTrue(ts.contains("name: string"));
        assertTrue(ts.contains("age: number"));
        assertTrue(ts.contains("active: boolean"));
    }

    @Test
    @DisplayName("Should convert a complex DTO to TypeScript")
    void testComplexDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(ComplexDto.class);
        assertNotNull(ts);
        assertTrue(ts.contains("id: string"));
        assertTrue(ts.contains("details: {"));
        assertTrue(ts.contains("tags: string[]"));
        assertTrue(ts.contains("scores: "));
        assertTrue(ts.contains("key: number"));
    }

    @Test
    @DisplayName("Should convert a nested list DTO to TypeScript")
    void testNestedListDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(NestedListDto.class);
        assertNotNull(ts);
        assertTrue(ts.contains("items: [{"));
        assertTrue(ts.contains("name: string"));
    }

    @Test
    @DisplayName("Should convert an Enum DTO to TypeScript")
    void testEnumDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(EnumDto.class);
        assertNotNull(ts);
        assertTrue(ts.contains("status: 'ACTIVE' | 'INACTIVE' | 'PENDING'"));
    }

    @Test
    @DisplayName("Should convert a recursive DTO to TypeScript")
    void testRecursiveDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(RecursiveDto.class);
        assertNotNull(ts);
        assertTrue(ts.contains("...recursive reference..."));
    }

    @Test
    @DisplayName("Should convert Date/Time and UUID DTO to TypeScript")
    void testDateDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(DateDto.class);
        assertNotNull(ts);
        assertTrue(ts.contains("createdAt: string //Time"));
        assertTrue(ts.contains("eventDate: string //Time"));
        assertTrue(ts.contains("eventTime: string //Time"));
        assertTrue(ts.contains("uuid: string //UUID"));
    }

    @Test
    @DisplayName("Should convert primitive array DTO to TypeScript")
    void testPrimitiveArrayDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(PrimitiveArrayDto.class);
        assertNotNull(ts);
        assertTrue(ts.contains("numbers: number[]"));
        assertTrue(ts.contains("names: string[]"));
    }

    @Test
    @DisplayName("Should convert object array DTO to TypeScript")
    void testObjectArrayDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(ObjectArrayDto.class);
        assertNotNull(ts);
        assertTrue(ts.contains("simpleDto: [{"));
        assertTrue(ts.contains("name: string"));
    }

    @Test
    @DisplayName("Should convert object array DTO to TypeScript")
    void testArrayDtoToTypeScript() {
        String ts = typeToTypeScript.generateJson(SimpleDto2.class);
        assertNotNull(ts);
        assertTrue(ts.contains("name: string[]"));
        assertTrue(ts.contains("uuid: string[] //UUID"));
        assertTrue(ts.contains("url: string[] //URL or URI"));
        assertTrue(ts.contains("date: string[] //Time"));
    }
}