# WebSocket Documentation Library - Developer Guide

This document provides a detailed guide for developers integrating or contributing to the WebSocket Documentation Library.

---

## 📦 Project Overview

This project aims to provide a lightweight and easy-to-integrate solution for documenting WebSocket APIs in Spring Boot applications. It automatically generates interactive documentation for your WebSocket message payloads, supporting both JSON example data and TypeScript type definitions.

---

## ✨ Features

- **Annotation-Driven Documentation:** Easily document your WebSocket topics using the `@WebSocketTopic` annotation.
- **Flexible Payload Schema Generation:**
    - **Default JSON Examples:** Automatically generates pretty-printed JSON examples for your message payloads using `TypeToJson`.
    - **TypeScript Type Definitions:** Can be configured to generate TypeScript-friendly type definitions using `TypeToTypeScript`.
        - Primitive Java types (e.g., `String`, `int`, `boolean`) are mapped to their corresponding TypeScript types (`string`, `number`, `boolean`).
        - Collections (`java.util.List`, arrays) are represented as `Type[]` (e.g., `string[]`, `number[]`).
        - Lists of Enum types are formatted as `('ENUM_VAL1', 'ENUM_VAL2')[]`.
        - Nested objects are recursively explored to show their full structure, generating TypeScript object literal-like output.
        - Special types like `UUID`, `LocalDateTime`, `LocalDate`, `LocalTime` are mapped to `string` with inline comments (e.g., `string //UUID`).
- **Interactive Web UI:** Provides a simple web interface (`webSocketDocUi.html`) to browse documented WebSocket topics.
- **Copy-to-Clipboard Functionality:** Conveniently copy topic names and payload schemas directly from the UI.
- **Spring Boot Auto-configuration:** Designed for seamless integration with Spring Boot projects via `spring.factories`, providing sensible defaults and easy customization through `WebSocketDocsAutoConfiguration`.
- **Robust Exception Handling:** Custom exception types (`AnnotationScanException`, `DocGenException`) provide clear error reporting during annotation scanning and documentation generation.

---

## 🚀 Getting Started (for Library Users)

### 1. Add the Dependency

To integrate this library into your Spring Boot project, add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'io.github.mini0192:websocket-doc-ui:0.1.5' // Use the latest version
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

The library automatically scans for `@WebSocketTopic` annotations within your Spring Boot application's component scan paths. You can configure the base package for scanning your **WebSocket DTOs** using `websocket.doc.basePackage` in your `application.properties` or `application.yml`.

**Important:** Spring Boot's auto-configuration typically handles the scanning of the library's components (like controllers and managers) if your main application class is located in a parent package of `com.websocket.core`. However, if you explicitly define `@ComponentScan` on your main application class and your application's base package is not a parent of `com.websocket.core`, you might need to include `com.websocket.core` in your `@ComponentScan` annotation to ensure the library's components are discovered.

**Example `application.properties`:**
```properties
websocket.doc.basePackage=com.example.websocket
```

**Example `@ComponentScan` usage (if needed):**
```java
@SpringBootApplication
@ComponentScan({"com.example.yourproject", "com.websocket.core"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

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

### 4. Access the Documentation UI

Once your application is running, open your browser and navigate to:
```
http://localhost:8080/websocket-docs
```
(This assumes your application is running on port 8080.)

---

## 5. Using the Test Client

The library provides an interactive WebSocket test client accessible via the web UI. This client allows you to connect to your WebSocket endpoint, subscribe to topics, and send messages directly from your browser.

1.  **Access the Test Client:**
    Navigate to `http://localhost:8080/websocket-docs` (or your application's base URL) and click on the "Test Client" tab.

2.  **Connect to WebSocket:**
    -   Enter your WebSocket endpoint URL in the "WebSocket Endpoint" field (e.g., `ws://localhost:8080/websocket` or `http://localhost:8080/websocket` if using SockJS).
    -   Click the "Connect" button. The log area will show a "Connection EndPoint: [your-endpoint]" message upon successful connection.

3.  **Subscribe to a Topic:**
    -   In the "Subscribe Topic" field, enter the WebSocket topic you wish to subscribe to (e.g., `/topic/chat.publicMessages`).
    -   Click the "Subscribe" button. You will see a "Subscribed to: [topic]" message in the log. Any messages sent to this topic by your server will appear in the log.

4.  **Send a Message:**
    -   In the "Send To" field, enter the application destination to which you want to send a message (e.g., `/app/hello`).
    -   Enter the message content in the "Message Content" textarea.
    -   Click the "Send" button. A "Send To: [destination]" message will appear in the log.

5.  **View Logs:**
    All connection, subscription, and message activities are logged in the "Log" area, providing real-time feedback on your WebSocket interactions.

---

## 🛠 Internal Architecture (for Contributors)

The library's internal architecture is designed for modularity and extensibility.

### 🔍 Annotation Processing
- `com.websocket.annotation.WebSocketTopic`: Core annotation for marking WebSocket response DTOs for documentation. It includes `topic`, `description`, and `group` attributes.
- `com.websocket.annotation.AnnotationScanner`: Scans the classpath for classes annotated with `@WebSocketTopic` within a specified base package. It uses Spring's `ClassPathScanningCandidateComponentProvider` and throws `AnnotationScanException` if scanning fails or the package name is not set.

### 🧱 Payload Serialization
- `com.websocket.core.serializer.TypeSerializer`: An interface defining the contract for converting Java classes into a string representation (e.g., JSON or TypeScript). The `generateJson` method is the primary entry point.
- `com.websocket.core.serializer.AbstractTypeSerializer`: An abstract base class that provides common logic for traversing Java class structures, handling nested objects, collections (Lists, Maps, Arrays), and circular references using `IdentityHashMap` for cycle detection. It uses Jackson's `ObjectMapper` for JSON processing and throws `DocGenException` on serialization failures. Subclasses must implement `getPrimitiveOrSimpleValue` to define how primitive and simple types are represented.
- `com.websocket.core.serializer.TypeToJson`: A concrete implementation of `TypeSerializer` that extends `AbstractTypeSerializer`. It generates pretty-printed JSON example data, providing sensible default values for various Java types including primitives, `String`, `UUID`, `java.time` classes, and enums.
- `com.websocket.core.serializer.TypeToTypeScript`: A concrete implementation of `TypeSerializer` that extends `AbstractTypeSerializer`. It generates TypeScript type definitions, including special handling for enum lists (e.g., `('ENUM_VAL1', 'ENUM_VAL2')[]`) and inline comments for specific Java types (e.g., `string //UUID`). It overrides `convertMapToString` to format the output as TypeScript object literals.

### 💡 UI and Controller
- `com.websocket.core.controller.BasicWebSocketDocController`: A Spring MVC `@Controller` that handles the `/websocket-docs` endpoint. It retrieves grouped WebSocket topic metadata from `WebSocketDocManager` and adds it to the `Model` for rendering by the `webSocketDocUi.html` Thymeleaf template.
- `src/main/resources/templates/webSocketDocUi.html`: The Thymeleaf-based UI template responsible for displaying topics, descriptions, and schemas, with copy-to-clipboard support.

### ⚙️ Auto-configuration
- `com.websocket.core.WebSocketDocsAutoConfiguration`: This Spring `@Configuration` class provides auto-configuration for the library.
    - It uses `@EnableConfigurationProperties(WebSocketUiConfig.class)` to bind properties prefixed with `websocket.doc` to the `WebSocketUiConfig` bean, allowing configuration of the base package for scanning.
    - It defines a default `TypeSerializer` bean (`TypeToJson`) using `@ConditionalOnMissingBean`, ensuring that `TypeToJson` is used unless another `TypeSerializer` bean is explicitly provided by the user.
    - It defines an `AnnotationScanner` bean, initialized with the `basePackage` from `WebSocketUiConfig`.

### ⚠️ Exception Handling
- `com.websocket.exception.AnnotationScanException`: A `RuntimeException` thrown during issues with annotation scanning, such as an unset package name or failure to load a scanned class.
- `com.websocket.exception.DocGenException`: A `RuntimeException` thrown when there are failures during the documentation generation process, typically within the `TypeSerializer` implementations.

---

## 🤝 Contributing
We welcome all contributions!
If you have suggestions, bug reports, or new features, follow these steps:

1. Fork the repository.
2. Create a new branch (e.g., `v{version}/feat/{issue number}` or `v{version}/fix/{issue number}`).
3. Make your changes.
4. Ensure all tests pass and write new ones as needed. You can run tests using:
   ```bash
   ./gradlew test
   ```
5. Commit your changes (e.g., `feat: Add new feature`).
6. Push the branch (e.g., `git push origin feature/your-feature-name`).
7. Open a Pull Request.

## 📄 License
This project is licensed under the terms of the [LICENSE](LICENSE) file.

## 📞 Contact
- **Email**: parkgw9071@gmail.com
- **GitHub**: mini0192
