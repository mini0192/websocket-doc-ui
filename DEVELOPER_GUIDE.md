# WebSocket Documentation Library - Developer Guide

This document provides a detailed guide for developers integrating or contributing to the WebSocket Documentation Library.

---

## 📦 Project Overview

This project aims to provide a lightweight and easy-to-integrate solution for documenting WebSocket APIs in Spring Boot applications.

---

## ✨ Features

- **Annotation-Driven Documentation:** Easily document your WebSocket topics using the `@WebSocketTopic` annotation.
- **Automatic Schema Generation:** Generates TypeScript-friendly JSON schemas for your message payloads.
    - Primitive Java types (e.g., `String`, `int`, `boolean`) are mapped to their corresponding TypeScript types (`string`, `number`, `boolean`).
    - Collections (`java.util.List`, arrays) are represented as `[Type]` (e.g., `[string]`, `[number]`).
    - Nested objects are recursively explored to show their full structure, generating TypeScript object literal-like output.
- **Interactive Web UI:** Provides a simple web interface (`webSocketDocUi.html`) to browse documented WebSocket topics.
- **Copy-to-Clipboard Functionality:** Conveniently copy topic names and payload schemas directly from the UI.
- **Spring Boot Auto-configuration:** Designed for seamless integration with Spring Boot projects via `spring.factories`.

---

## 🚀 Getting Started (for Library Users)

### 1. Add the Dependency

To integrate this library into your Spring Boot project, add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'io.github.mini0192:websocket-doc-ui:0.0.2'
}
```

### 2. Document Your WebSocket Response Payloads
   Use the @WebSocketTopic annotation on your DTO/message classes that represent the response payloads sent by the server to WebSocket clients.
   This annotation defines the destination topic and a brief description of the payload.

| ⚠️ Note: The annotation is intended to document response messages, not incoming messages from clients.

```java
package com.example.websocket.dto;

import com.websocket.annotation.WebSocketTopic;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@WebSocketTopic(topic = "/topic/chat.publicMessages", description = "Payload for public chat messages.")
class PublicChatMessage {
    private String sender;
    private String message;
}

@Getter
@Setter
@WebSocketTopic(topic = "/app/status.update", description = "Payload for updating user status with a list of items.")
class StatusUpdate {
    private String userId;
    private List<String> items;
    private int level;
}
```

### 3. Configure Package Scanning (⚠ Required)
You must explicitly configure the base package for the annotation scanner to find your DTO classes.
This should be done in your Spring Boot application's main class:
```java
@SpringBootApplication
@ComponentScan({"com.test", "com.websocket.core"}) // Include both your package and the library core
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);

        // Set the package for scanning @WebSocketTopic annotations
        String basePackage = TestApplication.class.getPackage().getName();
        AnnotationScanner.setPackageName(basePackage);
    }
}
```

| 🔴 If this step is skipped, no topics will appear in the documentation.

### 4. Access the Documentation UI
Once your application is running, open your browser and navigate to:
```
http://localhost:8080/websocket-docs
```
(This assumes your application is running on port 8080.)


## 🛠 Internal Architecture (for Contributors)

### 🔍 Annotation Processing
- com.websocket.annotation.WebSocketTopic: Core annotation for marking WebSocket response DTOs for documentation.
- com.websocket.annotation.AnnotationScanner: Scans Spring-managed beans for classes annotated with @WebSocketTopic.

### 🧱 Schema Generation
- com.websocket.core.classtojson.ClassToJson: Interface for converting Java classes into JSON-like schemas.
- com.websocket.core.classtojson.BasicClassToJson: Concrete implementation that generates TypeScript-style schemas by recursively converting Java types (primitives, wrappers, lists, arrays, nested objects).

### 💡 UI and Controller
- com.websocket.core.controller.BasicWebSocketDocController: A Spring MVC controller that provides the documentation data and serves the UI page.
- src/main/resources/templates/webSocketDocUi.html: The Thymeleaf-based UI template displaying topics, descriptions, and schemas, with copy-to-clipboard support.


## 🤝 Contributing
We welcome all contributions!
If you have suggestions, bug reports, or new features, follow these steps:

1. Fork the repository.
2. Create a new branch (git checkout -b feat/your-feature-name or fix/your-bug-name).
3. Make your changes.
4. Ensure all tests pass and write new ones as needed.
5. Commit your changes (git commit -m "feat: Add new feature").
6. Push the branch (git push origin feature/your-feature-name).
7. Open a Pull Request.

## 📄 License
This project is licensed under the terms of the [LICENSE](LICENSE) file.
