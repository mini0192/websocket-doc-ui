package com.websocket.core;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebSocketDocsController {

    private final ApplicationContext context;

    @GetMapping("/websocket-docs")
    public String websocketDocs(Model model) {
        WebSocketMeta meta = new WebSocketDocManager(context).buildMeta();
        model.addAttribute("docs", meta);
        return "webSocketDocUi";
    }
}
