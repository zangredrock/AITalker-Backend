package com.rocky.coordinatorservice.model;

import lombok.Data;

import java.util.List;

@Data
public class SessionMessage {

    private String sessionId;

    private String userId;

    private List<AIMessage> messages;

}
