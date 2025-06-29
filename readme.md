# websocket-doc-ui

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

## Overview

`websocket-doc-ui` is a documentation tool for WebSocket APIs that runs exclusively on Spring Boot projects.  
It was created to address the lack of open-source solutions for documenting WebSocket endpoints.

This library helps you easily document and manage your WebSocket APIs, improving development productivity and collaboration.

---

## Features

- Automatic WebSocket API documentation for Spring Boot applications
- Intuitive UI for easy exploration of WebSocket endpoints
- Extensible architecture to support custom features

---

## Installation & Usage

### 1. Add dependency to your Gradle build

```gradle
dependencies {
    implementation("io.github.mini0192:websocket-doc-ui:0.0.1")
}
```
## Integration Guide
```java
@SpringBootApplication
@ComponentScan({"com.test", "com.websocket.core"}) // Add the library package here
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
```
- com.test is your application’s base package.
- com.websocket.core is the package from the websocket-doc-ui library.

Including both packages in @ComponentScan ensures Spring detects components from your app and the library.


## Contributing
- Open an issue on GitHub Issues to request features or report bugs. 
- Make changes based on the issue.
- Submit a Pull Request (PR) for review. 
- After review, your changes will be merged.

Thank you for contributing!

## License
This project is licensed under the Apache License 2.0.
See the LICENSE file for details.

## Contact
- **Email**: parkgw9071@gmail.com
- **GitHub**: mini0192