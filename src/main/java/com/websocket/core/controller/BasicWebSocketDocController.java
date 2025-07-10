package com.websocket.core.controller;

import com.websocket.core.WebSocketTopicMeta;
import com.websocket.core.websocketdocmanager.WebSocketDocManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BasicWebSocketDocController {

    private final WebSocketDocManager webSocketDocManager;

    @GetMapping("/websocket-docs")
    public String webSocketDocs(Model model) {
        Map<String, List<WebSocketTopicMeta>> groupedMetas = webSocketDocManager.getTopicMeta();
        model.addAttribute("groupedDocs", groupedMetas);
        return "webSocketDocUi";
    }
}
