package com.websocket.core.websocketdocmanager;

import com.websocket.annotation.WebSocketTopic;
import com.websocket.core.WebSocketTopicMeta;
import com.websocket.annotation.AnnotationScanner;
import com.websocket.core.serializer.TypeSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class BasicWebSocketDocManager implements WebSocketDocManager {

    private final TypeSerializer typeSerializer;
    private final AnnotationScanner annotationScanner;

    @Override
    public Map<String, List<WebSocketTopicMeta>> getTopicMeta() {
        Map<String, List<WebSocketTopicMeta>> groupedTopics = new LinkedHashMap<>();
        Set<Class<?>> classes = annotationScanner.findAnnotatedClasses(WebSocketTopic.class);

        for(Class<?> clazz : classes) {
            WebSocketTopic topicAnnotation = clazz.getAnnotation(WebSocketTopic.class);
            if(topicAnnotation != null) {
                WebSocketTopicMeta meta = WebSocketTopicMeta.builder()
                        .topic(topicAnnotation.topic())
                        .description(topicAnnotation.description())
                        .payload(typeSerializer.generateJson(clazz))
                        .build();

                groupedTopics.computeIfAbsent(topicAnnotation.group(), k -> new ArrayList<>()).add(meta);
            }
        }
        return groupedTopics;
    }
}
