package com.websocket.core;

import com.websocket.annotation.WebSocketController;
import com.websocket.annotation.WebSocketDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class WebSocketDocManager {

    private final ApplicationContext context;

    private String buildPayload(Method method) {
        String payload = null;

        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Payload.class)) {
                ClassToJson classToJson = new ClassToJson();
                Class<?> paramType = parameter.getType();
                payload = classToJson.generateExampleJson(paramType);
                break;
            }
        }

        return payload;
    }

    private List<WebSocketMeta.App> buildApps(Object bean) {
        List<WebSocketMeta.App> apps = new ArrayList<>();

        for (Method method : bean.getClass().getMethods()) {
            if (method.isAnnotationPresent(WebSocketDocs.class) && method.isAnnotationPresent(MessageMapping.class)) {

                WebSocketDocs websocketDocs = method.getAnnotation(WebSocketDocs.class);
                MessageMapping messageMapping = method.getAnnotation(MessageMapping.class);

                apps.add(WebSocketMeta.App.builder()
                        .topic(websocketDocs.topic())
                        .app(Arrays.toString(messageMapping.value()))
                        .description(websocketDocs.description())
                        .payload(buildPayload(method))
                        .build());
            }
        }

        return apps;
    }

    public WebSocketMeta buildMeta() {
        List<WebSocketMeta.Group> groups = new ArrayList<>();
        String[] beanNames = context.getBeanNamesForAnnotation(WebSocketController.class);

        for (String beanName : beanNames) {
            groups.add(WebSocketMeta.Group.builder()
                    .name(beanName)
                    .app(buildApps(context.getBean(beanName)))
                    .build());
        }

        return WebSocketMeta.builder()
                .group(groups)
                .build();
    }
}
