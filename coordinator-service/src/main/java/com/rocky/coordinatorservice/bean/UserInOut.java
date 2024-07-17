package com.rocky.coordinatorservice.bean;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserInOut {

    private String userId;

    private String userName;

    private String sessionId;

    private String sessionName;

    private String audio;

    private String text;

    private MultipartFile file;

}
