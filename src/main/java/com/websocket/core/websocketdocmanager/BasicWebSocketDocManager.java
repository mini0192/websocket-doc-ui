package com.websocket.core.websocketdocmanager;

import com.websocket.annotation.WebSocketTopic;
import com.websocket.core.classtojson.ClassToJson;
import com.websocket.core.WebSocketTopicMeta;
import com.websocket.annotation.AnnotationScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.util.*;

@RequiredArgsConstructor
public class BasicWebSocketDocManager implements WebSocketDocManager {

    private final ApplicationContext context;
    private final ClassToJson classToJson;

    @Override
    public List<WebSocketTopicMeta> getTopicMeta() {
        List<WebSocketTopicMeta> topics = new ArrayList<>();
        Set<Class<?>> classes = AnnotationScanner.findAnnotatedClasses(WebSocketTopic.class);
        for(Class<?> clazz : classes) {
            WebSocketTopic topicAnnotation = clazz.getAnnotation(WebSocketTopic.class);
            if(topicAnnotation != null) {
                topics.add(WebSocketTopicMeta.builder()
                        .topic(topicAnnotation.topic())
                        .description(topicAnnotation.description())
                        .payload(classToJson.generateJson(clazz))
                        .build());
            }
        }
        return topics;
    }
}
