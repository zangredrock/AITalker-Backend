package com.rocky.coordinatorservice.client;

import com.rocky.coordinatorservice.model.SessionMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("aichat-service")
public interface AiChatServiceClient {

    @PostMapping("/aichat/chatback")
    String chatback(@RequestBody SessionMessage sessionMessage);

}