package com.websocket.core.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websocket.exception.DocGenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TypeToJsonTest {

    private final TypeToJson typeToJson = new TypeToJson();

    // Test DTOs
    static class SimpleDto {
        public String name;
        public int age;
        public boolean active;
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

    private String toNormalizedJson(String json) {
        return json.replace("\n", "")
                .replace("\r", "")
                .replace(" ", "");
    }

    @Test
    @DisplayName("Should convert a simple DTO to JSON")
    void testSimpleDtoToJson() {
        String json = typeToJson.generateJson(SimpleDto.class);
        assertNotNull(json);

        json = toNormalizedJson(json);
        String expectedJson = """
        {
          "name": "string",
          "age": 0,
          "active": false
        }
        """;
        expectedJson = toNormalizedJson(expectedJson);
        assertEquals(expectedJson.trim(), json.trim());
    }

    @Test
    @DisplayName("Should convert a complex DTO to JSON")
    void testComplexDtoToJson() {
        String json = typeToJson.generateJson(ComplexDto.class);
        assertNotNull(json);

        json = toNormalizedJson(json);
        String expectedJson = """
        {
          "id" : "string",
          "details" : {
            "name" : "string",
            "age" : 0,
            "active" : false
          },
          "tags" : [ "string" ],
          "scores" : {
            "key" : 0
          }
        }
        """;
        expectedJson = toNormalizedJson(expectedJson);
        assertEquals(expectedJson.trim(), json.trim());
    }

    @Test
    @DisplayName("Should convert a nested list DTO to JSON")
    void testNestedListDtoToJson() {
        String json = typeToJson.generateJson(NestedListDto.class);
        assertNotNull(json);

        json = toNormalizedJson(json);
        String expectedJson = """
        {
          "items" : [ {
            "name" : "string",
            "age" : 0,
            "active" : false
          } ]
        }
        """;
        expectedJson = toNormalizedJson(expectedJson);
        assertEquals(expectedJson.trim(), json.trim());
    }

    @Test
    @DisplayName("Should convert an Enum DTO to JSON")
    void testEnumDtoToJson() {
        String json = typeToJson.generateJson(EnumDto.class);
        assertNotNull(json);

        json = toNormalizedJson(json);
        String expectedJson = """
        {
          "status" : "ACTIVE"
        }
        """;
        expectedJson = toNormalizedJson(expectedJson);
        assertEquals(expectedJson.trim(), json.trim());
    }

    @Test
    @DisplayName("Should convert a recursive DTO to JSON")
    void testRecursiveDtoToJson() {
        String json = typeToJson.generateJson(RecursiveDto.class);
        assertNotNull(json);

        json = toNormalizedJson(json);
        String expectedJson = """
        {
          "name" : "string",
          "selfRef" : {
            "...recursive reference..." : "RecursiveDto"
          }
        }
        """;
        expectedJson = toNormalizedJson(expectedJson);
        assertEquals(expectedJson.trim(), json.trim());
    }

    @Test
    @DisplayName("Should convert Date/Time and UUID DTO to JSON with correct formats")
    void testDateDtoToJson_formatCheck() throws Exception {
        String json = typeToJson.generateJson(DateDto.class);
        assertNotNull(json);

        JsonNode root = new ObjectMapper().readTree(json);

        assertTrue(root.has("createdAt"));
        assertTrue(root.get("createdAt").asText()
                        .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+"),
                "createdAt must be ISO-8601 with nanoseconds");

        assertTrue(root.has("eventDate"));
        assertTrue(root.get("eventDate").asText()
                        .matches("\\d{4}-\\d{2}-\\d{2}"),
                "eventDate must be yyyy-MM-dd");

        assertTrue(root.has("eventTime"));
        assertTrue(root.get("eventTime").asText()
                        .matches("\\d{2}:\\d{2}:\\d{2}\\.\\d+"),
                "eventTime must be HH:mm:ss.nnnnnnnnn");

        assertTrue(root.has("uuid"));
        assertTrue(root.get("uuid").asText()
                        .matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"),
                "uuid must be a valid UUID format");
    }


    @Test
    @DisplayName("Should convert primitive array DTO to JSON")
    void testPrimitiveArrayDtoToJson() {
        String json = typeToJson.generateJson(PrimitiveArrayDto.class);
        assertNotNull(json);

        json = toNormalizedJson(json);
        String expectedJson = """
        {
          "numbers" : [ 0 ],
          "names" : [ "string" ]
        }
        """;
        expectedJson = toNormalizedJson(expectedJson);
        assertEquals(expectedJson.trim(), json.trim());
    }

    @Test
    @DisplayName("Should convert object array DTO to JSON")
    void testObjectArrayDtoToJson() {
        String json = typeToJson.generateJson(ObjectArrayDto.class);
        assertNotNull(json);

        json = toNormalizedJson(json);
        String expectedJson = """
        {
          "simpleDto" : [ {
            "name" : "string",
            "age" : 0,
            "active" : false
          } ]
        }
        """;
        expectedJson = toNormalizedJson(expectedJson);
        assertEquals(expectedJson.trim(), json.trim());
    }
}
