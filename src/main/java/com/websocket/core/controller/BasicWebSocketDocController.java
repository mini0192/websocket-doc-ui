package com.websocket.core.controller;

import com.websocket.core.WebSocketTopicMeta;
import com.websocket.core.classtojson.ClassToJson;
import com.websocket.core.websocketdocmanager.BasicWebSocketDocManager;
import com.websocket.core.websocketdocmanager.WebSocketDocManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BasicWebSocketDocController {

    private final ApplicationContext context;
    private final ClassToJson classToJson;

    @GetMapping("/websocket-docs")
    public String webSocketDocs(Model model) {
        WebSocketDocManager webSocketDocManager = new BasicWebSocketDocManager(context, classToJson);
        List<WebSocketTopicMeta> metas = webSocketDocManager.getTopicMeta();
        model.addAttribute("docs", metas);
        return "webSocketDocUi";
    }
}
