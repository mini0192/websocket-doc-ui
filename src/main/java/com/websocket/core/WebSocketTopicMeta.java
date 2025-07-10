package com.websocket.core;

import lombok.Builder;

@Builder
public record WebSocketTopicMeta(
        String topic,
        String description,
        String payload
) { }
