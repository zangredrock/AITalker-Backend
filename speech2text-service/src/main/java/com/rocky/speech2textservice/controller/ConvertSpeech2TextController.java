package com.rocky.speech2textservice.controller;

import com.rocky.speech2textservice.bean.UserInOut;
import com.rocky.speech2textservice.service.ConvertSpeech2TextService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transcribe")
public class ConvertSpeech2TextController {

    @Autowired
    private ConvertSpeech2TextService convertSpeech2TextService;

    //@GetMapping("/{base64Audio}")
    @PostMapping(path = "/convert",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public String transcribe(@RequestBody UserInOut userInOut, HttpServletRequest request) {
        return this.convertSpeech2TextService.convertSpeech2Text(userInOut.getAudio());
    }
}
