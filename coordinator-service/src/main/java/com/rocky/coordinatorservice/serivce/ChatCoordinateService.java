package com.rocky.coordinatorservice.serivce;

import com.rocky.coordinatorservice.bean.UserInOut;
import com.rocky.coordinatorservice.model.SessionMessage;
import com.rocky.coordinatorservice.model.User;

public interface ChatCoordinateService {

    public UserInOut coordinateChat(UserInOut input);

    public User getUserSession(UserInOut input);

    public SessionMessage createSession(UserInOut input);
}
