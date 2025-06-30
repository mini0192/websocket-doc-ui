package com.websocket.core.controller;

import com.websocket.core.WebSocketMeta;
import com.websocket.core.websocketdocmanager.BasicWebSocketDocManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BasicWebSocketDocController {

    private final ApplicationContext context;

    @GetMapping("/websocket-docs")
    public String webSocketDocs(Model model) {
        WebSocketMeta meta = new BasicWebSocketDocManager(context).buildMeta();
        model.addAttribute("docs", meta);
        return "webSocketDocUi";
    }
}
