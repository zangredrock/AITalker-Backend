package com.rocky.speech2textservice.bean;

import lombok.Data;

@Data
public class UserInOut {

    private String userId;

    private String userName;

    private String sessionId;

    private String sessionName;

    private String audio;

    private String text;

}
