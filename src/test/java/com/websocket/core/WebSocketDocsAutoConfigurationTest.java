package com.websocket.core;

import com.websocket.core.serializer.TypeToJson;
import com.websocket.core.serializer.TypeToTypeScript;
import com.websocket.core.serializer.TypeSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketDocsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebSocketDocsAutoConfiguration.class));

    @Test
    @DisplayName("Default configuration: TypeToJson should be injected as TypeSerializer")
    void testDefaultTypeToJsonBean() {
        this.contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(TypeSerializer.class);
                    assertThat(context).hasSingleBean(TypeToJson.class);
                    assertThat(context).doesNotHaveBean(TypeToTypeScript.class);
                    assertThat(context.getBean(TypeSerializer.class)).isInstanceOf(TypeToJson.class);
                });
    }

    @Test
    @DisplayName("User-defined configuration: TypeToTypeScript should be injected as TypeSerializer")
    void testUserDefinedTypeToTypeScriptBean() {
        this.contextRunner
                .withUserConfiguration(UserDefinedTypeToTypeScriptConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(TypeSerializer.class);
                    assertThat(context).hasSingleBean(TypeToTypeScript.class);
                    assertThat(context).doesNotHaveBean(TypeToJson.class);
                    assertThat(context.getBean(TypeSerializer.class)).isInstanceOf(TypeToTypeScript.class);
                });
    }

    @Configuration
    static class UserDefinedTypeToTypeScriptConfig {
        @Bean
        public TypeSerializer customTypeSerializer() {
            return new TypeToTypeScript();
        }
    }
}