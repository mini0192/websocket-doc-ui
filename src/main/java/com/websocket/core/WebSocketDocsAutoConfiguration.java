package com.websocket.core;

import com.websocket.WebSocketUiConfig;
import com.websocket.annotation.AnnotationScanner;
import com.websocket.core.serializer.TypeToJson;
import com.websocket.core.serializer.TypeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WebSocketUiConfig.class)
public class WebSocketDocsAutoConfiguration {

    private final WebSocketUiConfig webSocketUiConfig;

    public WebSocketDocsAutoConfiguration(WebSocketUiConfig webSocketUiConfig) {
        this.webSocketUiConfig = webSocketUiConfig;
    }
    /**
     * Defines the default TypeSerializer bean.
     * This bean will be created only if no other bean of type TypeSerializer is present in the context.
     * The default implementation provides JSON example data.
     * @return The default TypeToJson serializer.
     */
    @Bean
    @ConditionalOnMissingBean(TypeSerializer.class)
    public TypeSerializer typeSerializer() {
        return new TypeToJson();
    }

    @Bean
    @ConditionalOnMissingBean(AnnotationScanner.class)
    public AnnotationScanner annotationScanner() {
        return new AnnotationScanner(webSocketUiConfig.getBasePackage());
    }
}
