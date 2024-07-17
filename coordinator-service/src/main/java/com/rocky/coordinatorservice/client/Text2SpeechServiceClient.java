package com.rocky.coordinatorservice.client;

import com.rocky.coordinatorservice.bean.UserInOut;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("text2speech-service")
public interface Text2SpeechServiceClient {

    @PostMapping("/synthesize/go")
    UserInOut convertText2Speech(@RequestBody UserInOut userInOut);

}
