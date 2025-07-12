package com.websocket.core;

import com.websocket.core.serializer.TypeToJson;
import com.websocket.core.serializer.TypeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketDocsAutoConfiguration {

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
}
