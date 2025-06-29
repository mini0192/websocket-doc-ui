package com.websocket.core;

import lombok.Builder;

import java.util.List;

@Builder
public record WebSocketMeta (
        List<Group> group
) {
    @Builder
    public record Group (
            String name,
            List<App> app
    ) {}

    @Builder
    public record App (
            String app,
            String topic,
            String description,
            String payload
    ) {}
}
