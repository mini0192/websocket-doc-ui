package com.websocket.annotation;

import com.websocket.exception.AnnotationScanException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationScannerTest {

    // Test annotations and classes
    @WebSocketTopic(topic = "/test/topic1", description = "Desc1")
    static class TestTopicClass1 {}

    @WebSocketTopic(topic = "/test/topic2", group = "groupA")
    static class TestTopicClass2 {}

    static class NonAnnotatedClass {}

    @Test
    @DisplayName("Should throw exception when package name is not set")
    void testThrowsExceptionWhenPackageNameNotSet() {
        assertThrows(AnnotationScanException.class, () -> new AnnotationScanner(null));
    }

    @Test
    @DisplayName("Should find classes annotated with the specified annotation")
    void testFindAnnotatedClasses() {
        // Set package name to scan this test class's package
        AnnotationScanner scanner = new AnnotationScanner(this.getClass().getPackage().getName());

        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClasses(WebSocketTopic.class);

        assertNotNull(annotatedClasses);
        assertEquals(2, annotatedClasses.size());
        assertTrue(annotatedClasses.contains(TestTopicClass1.class));
        assertTrue(annotatedClasses.contains(TestTopicClass2.class));
        assertFalse(annotatedClasses.contains(NonAnnotatedClass.class));
    }

    @Test
    @DisplayName("Should return empty set when scanning a non-existent package")
    void testScanNonExistentPackageReturnsEmptySet() {
        AnnotationScanner scanner = new AnnotationScanner("com.non.existent.package");
        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClasses(WebSocketTopic.class);
        assertNotNull(annotatedClasses);
        assertTrue(annotatedClasses.isEmpty());
    }

    @Test
    @DisplayName("Should return empty set when scanning with a different annotation")
    void testScanWithDifferentAnnotationReturnsEmptySet() {
        AnnotationScanner scanner = new AnnotationScanner(this.getClass().getPackage().getName());
        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClasses(ComponentScan.class);
        assertNotNull(annotatedClasses);
        assertTrue(annotatedClasses.isEmpty());
    }
}