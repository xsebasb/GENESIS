package com.gesnesis.chatbot;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private List<String> chatHistory = new ArrayList<>();

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public String chatPage(Model model) {
        model.addAttribute("chatHistory", chatHistory);
        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message, Model model) {
        JSONObject response = chatService.sendMessage(message);

        // Añadiendo mensajes al historial de chat
        chatHistory.add("You: " + message);
        chatHistory.add("Bot: " + response.toString());

        model.addAttribute("chatHistory", chatHistory);
        return "chat";
    }
}
