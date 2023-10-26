package com.gesnesis.chatbot;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping("/startSession")
    public ResponseEntity<Map<String, Object>> startSession() {
        // Aquí, puedes llamar a algún método en tu servicio para iniciar una sesión
        chatService.init();  // Esto es solo un ejemplo, en realidad querrás modularizar esto
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Session started");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/send")
    public ResponseEntity<Void> handleUserMessage(@RequestParam String message) {
        String response = chatService.sendMessage(message);
        template.convertAndSend("/topic/messages", response);
        return ResponseEntity.ok().build();
    }
}

