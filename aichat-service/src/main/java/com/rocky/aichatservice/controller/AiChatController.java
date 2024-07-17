package com.rocky.aichatservice.controller;

import com.rocky.aichatservice.bean.SessionMessage;
import com.rocky.aichatservice.service.AIChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aichat")
public class AiChatController {

    @Autowired
    AIChatService aichatService;

    @GetMapping("/{userInput}")
    public String chatOnText(@PathVariable String userInput) {
        String response = this.aichatService.chat(userInput);
        return response;
    }

    @PostMapping(path = "/chatback",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String chatOnText(@RequestBody SessionMessage session, HttpServletRequest request) {
        String response = this.aichatService.chat(session);
        return response;
    }

    @GetMapping("/image/{userInput}")
    public String generateImage(@PathVariable String userInput) {
        String imageUrl = this.aichatService.generateImage(userInput);
        return imageUrl;
    }
}
