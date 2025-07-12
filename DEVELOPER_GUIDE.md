# WebSocket Documentation Library - Developer Guide

This document provides a detailed guide for developers integrating or contributing to the WebSocket Documentation Library.

---

## 📦 Project Overview

This project aims to provide a lightweight and easy-to-integrate solution for documenting WebSocket APIs in Spring Boot applications. It automatically generates interactive documentation for your WebSocket message payloads, supporting both JSON example data and TypeScript type definitions.

---

## ✨ Features

- **Annotation-Driven Documentation:** Easily document your WebSocket topics using the `@WebSocketTopic` annotation.
- **Flexible Payload Schema Generation:**
    - **Default JSON Examples:** Automatically generates pretty-printed JSON examples for your message payloads.
    - **TypeScript Type Definitions:** Can be configured to generate TypeScript-friendly type definitions instead of JSON examples.
        - Primitive Java types (e.g., `String`, `int`, `boolean`) are mapped to their corresponding TypeScript types (`string`, `number`, `boolean`).
        - Collections (`java.util.List`, arrays) are represented as `Type[]` (e.g., `string[]`, `number[]`).
        - Lists of Enum types are formatted as `('ENUM_VAL1', 'ENUM_VAL2')[]`.
        - Nested objects are recursively explored to show their full structure, generating TypeScript object literal-like output.
        - Special types like `UUID`, `LocalDateTime`, `LocalDate`, `LocalTime` are mapped to `string` with inline comments (e.g., `string //UUID`).
- **Interactive Web UI:** Provides a simple web interface (`webSocketDocUi.html`) to browse documented WebSocket topics.
- **Copy-to-Clipboard Functionality:** Conveniently copy topic names and payload schemas directly from the UI.
- **Spring Boot Auto-configuration:** Designed for seamless integration with Spring Boot projects via `spring.factories`, providing sensible defaults and easy customization.
- **Robust Exception Handling:** Custom exception types provide clear error reporting during annotation scanning and documentation generation.

---

## 🚀 Getting Started (for Library Users)

### 1. Add the Dependency

To integrate this library into your Spring Boot project, add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'io.github.mini0192:websocket-doc-ui:0.1.0' // Use the latest version
}
```

### 2. Document Your WebSocket Response Payloads

Use the `@WebSocketTopic` annotation on your DTO/message classes that represent the response payloads sent by the server to WebSocket clients. This annotation defines the destination topic and a brief description of the payload.

| ⚠️ Note: The annotation is intended to document response messages, not incoming messages from clients.

```java
package com.example.websocket.dto;

import com.websocket.annotation.WebSocketTopic;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@WebSocketTopic(topic = "/topic/chat.publicMessages", description = "Payload for public chat messages.")
class PublicChatMessage {
    private String sender;
    private String message;
    private LocalDateTime timestamp;
}

enum Status {
    ONLINE, OFFLINE, AWAY
}

@Getter
@Setter
@WebSocketTopic(topic = "/app/status.update", description = "Payload for updating user status with a list of items.", group = "Feat")
class StatusUpdate {
    private String userId;
    private List<String> items;
    private int level;
    private UUID transactionId;
    private List<Status> userStatuses; // Example of enum list
}
```

### 3. Configure Package Scanning and Output Format

The library automatically scans for `@WebSocketTopic` annotations within your Spring Boot application's component scan paths. Ensure your DTOs are within a package scanned by Spring (e.g., under your `@SpringBootApplication`'s base package).

**To switch the output format from JSON (default) to TypeScript:**

You can override the default `TypeSerializer` bean by providing your own. Create a configuration class in your project like this:

```java
package com.your.project.config; // Your project's package

import com.websocket.core.serializer.TypeSerializer;
import com.websocket.core.serializer.TypeToTypeScript;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to use TypeScript format for WebSocket documentation.
 */
@Configuration
public class WebSocketDocConfig {

    /**
     * Defines the TypeSerializer bean to generate TypeScript type definitions.
     * This bean will override the library's default JSON example generator.
     * @return An instance of TypeToTypeScript.
     */
    @Bean
    public TypeSerializer typeSerializer() {
        return new TypeToTypeScript();
    }
}
```
| 🔴 Important: If you explicitly set `AnnotationScanner.setPackageName()` in your main application, ensure it covers the packages where your `@WebSocketTopic` annotated DTOs reside. However, for most Spring Boot applications, component scanning handles this automatically.

### 4. Access the Documentation UI

Once your application is running, open your browser and navigate to:
```
http://localhost:8080/websocket-docs
```
(This assumes your application is running on port 8080.)

---

## 🛠 Internal Architecture (for Contributors)

The library's internal architecture is designed for modularity and extensibility.

### 🔍 Annotation Processing
- `com.websocket.annotation.WebSocketTopic`: Core annotation for marking WebSocket response DTOs for documentation.
- `com.websocket.annotation.AnnotationScanner`: Scans the classpath for classes annotated with `@WebSocketTopic`. It throws `AnnotationScanException` if scanning fails.

### 🧱 Payload Serialization
- `com.websocket.core.serializer.TypeSerializer`: An interface defining the contract for converting Java classes into a string representation (e.g., JSON or TypeScript).
- `com.websocket.core.serializer.AbstractTypeSerializer`: An abstract base class that provides common logic for traversing Java class structures, handling nested objects, collections (Lists, Maps, Arrays), and circular references. It uses `IdentityHashMap` for cycle detection and throws `DocGenException` on serialization failures.
- `com.websocket.core.serializer.TypeToJson`: A concrete implementation of `TypeSerializer` that extends `AbstractTypeSerializer`. It generates pretty-printed JSON example data using Jackson's `ObjectMapper`.
- `com.websocket.core.serializer.TypeToTypeScript`: A concrete implementation of `TypeSerializer` that extends `AbstractTypeSerializer`. It generates TypeScript type definitions, including special handling for enum lists and comments for specific Java types (e.g., `UUID`, `java.time` classes).

### 💡 UI and Controller
- `com.websocket.core.controller.BasicWebSocketDocController`: A Spring MVC controller that orchestrates the documentation data retrieval and serves the UI page.
- `src/main/resources/templates/webSocketDocUi.html`: The Thymeleaf-based UI template displaying topics, descriptions, and schemas, with copy-to-clipboard support.

### ⚙️ Auto-configuration
- `com.websocket.core.WebSocketDocsAutoConfiguration`: This Spring `@Configuration` class automatically registers the default `TypeSerializer` (TypeToJson) if no other `TypeSerializer` bean is found in the application context, ensuring seamless integration.

---

## 🤝 Contributing
We welcome all contributions!
If you have suggestions, bug reports, or new features, follow these steps:

1. Fork the repository.
2. Create a new branch (e.g., `v{version}/feat/{issue number}` or `v{version}/fix/{issue number}`).
3. Make your changes.
4. Ensure all tests pass and write new ones as needed.
5. Commit your changes (e.g., `feat: Add new feature`).
6. Push the branch (e.g., `git push origin feature/your-feature-name`).
7. Open a Pull Request.

## 📄 License
This project is licensed under the terms of the [LICENSE](LICENSE) file.