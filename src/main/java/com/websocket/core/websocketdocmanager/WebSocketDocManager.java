package com.websocket.core.websocketdocmanager;

import com.websocket.core.WebSocketTopicMeta;

import java.util.List;
import java.util.Map;

public interface WebSocketDocManager {
    Map<String, List<WebSocketTopicMeta>> getTopicMeta();
}
