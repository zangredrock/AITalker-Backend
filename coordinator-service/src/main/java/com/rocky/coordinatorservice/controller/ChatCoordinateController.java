package com.rocky.coordinatorservice.controller;

import com.rocky.coordinatorservice.bean.UserInOut;
import com.rocky.coordinatorservice.model.AIMessage;
import com.rocky.coordinatorservice.model.SessionMessage;
import com.rocky.coordinatorservice.persistence.DynamoDBAccess;
import com.rocky.coordinatorservice.serivce.ChatCoordinateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@CrossOrigin
@RequestMapping("/talk")
@RestController
public class ChatCoordinateController {

    @Autowired
    private ChatCoordinateService chatCoordinateService;

    @Autowired
    private DynamoDBAccess dynamoDBAccess;

    //@GetMapping("/{base64Audio}")
    @PostMapping(path = "/json",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInOut talkJson(@RequestBody UserInOut userInOut, HttpServletRequest request) {
        return this.chatCoordinateService.coordinateChat(userInOut);
    }

    @PostMapping(path = "/go",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInOut talk(@ModelAttribute UserInOut userInOut) throws IOException {
        String base64Audio = Base64.getEncoder().encodeToString(userInOut.getFile().getBytes());
        userInOut.setAudio(base64Audio);
        userInOut.setFile(null);
        return this.chatCoordinateService.coordinateChat(userInOut);
    }

    //TODO: rewrite this by unit test later
    @GetMapping("/test/{action}")
    public String talkTest(@PathVariable String action) {

        if ("get".equals(action)) {
            UserInOut userInOut = new UserInOut();
            userInOut.setUserId("Zoe");
            userInOut.setSessionId("session1");
            this.dynamoDBAccess.getSessionMessage(userInOut);
        }

        if ("save".equals(action)) {
            SessionMessage sessionMessage = new SessionMessage();
            sessionMessage.setSessionId("session3");
            sessionMessage.setUserId("Zoe");
            AIMessage aiMessage = new AIMessage();
            aiMessage.setContent("this is a test content ...");
            aiMessage.setRole("system");
            List<AIMessage> aiMessages = new ArrayList<>();
            aiMessages.add(aiMessage);
            sessionMessage.setMessages(aiMessages);
            this.dynamoDBAccess.saveItem(sessionMessage);
        }

        if ("update".equals(action)) {
            SessionMessage sessionMessage = new SessionMessage();
            sessionMessage.setSessionId("session3");
            sessionMessage.setUserId("Zoe");
            AIMessage aiMessage = new AIMessage();
            aiMessage.setContent("this is a user test content ...");
            aiMessage.setRole("user");
            List<AIMessage> aiMessages = new ArrayList<>();
            aiMessages.add(aiMessage);
            sessionMessage.setMessages(aiMessages);
            this.dynamoDBAccess.updateItem(sessionMessage);
        }
        return "success";
    }

}
