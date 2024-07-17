package com.rocky.text2speechservice.controller;

import com.rocky.text2speechservice.bean.UserInOut;
import com.rocky.text2speechservice.service.ConvertText2SpeechService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/synthesize")
public class ConvertText2SpeechController {

    @Autowired
    private ConvertText2SpeechService convertText2SpeechService;

    @GetMapping("/{text}")
    public String simpleConvertText2Speech(@PathVariable("text") String text) {
        byte[] audioBytes = this.convertText2SpeechService.convertText2Speech(text);
        String base64Audio = Base64.getEncoder().encodeToString(audioBytes);
        return base64Audio;
    }

    @PostMapping(path = "/go",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInOut convertText2Speech(@RequestBody UserInOut userInOut, HttpServletRequest requestt) {
        byte[] audioBytes = this.convertText2SpeechService.convertText2Speech(userInOut.getText());
        String base64Audio = Base64.getEncoder().encodeToString(audioBytes);
        userInOut.setAudio(base64Audio);
        return userInOut;
    }

}
