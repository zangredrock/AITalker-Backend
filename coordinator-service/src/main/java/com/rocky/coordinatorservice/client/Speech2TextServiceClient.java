package com.rocky.coordinatorservice.client;

import com.rocky.coordinatorservice.bean.UserInOut;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("speech2text-service")
public interface Speech2TextServiceClient {

    //@RequestMapping(value = "/transcribe/convert", method = RequestMethod.POST)
    @PostMapping("/transcribe/convert")
    String convertSpeech2Text(@RequestBody UserInOut userInOut);

}
