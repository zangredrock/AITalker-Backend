package com.rocky.aichatservice.service;

import com.rocky.aichatservice.bean.SessionMessage;

public interface AIChatService {

    public String chat(String message);

    public String generateImage(String message);

    public String chat(SessionMessage session);
}
