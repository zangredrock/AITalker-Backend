package com.rocky.coordinatorservice.servicetest;

import com.rocky.coordinatorservice.bean.UserInOut;
import com.rocky.coordinatorservice.model.User;
import com.rocky.coordinatorservice.serivce.ChatCoordinateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatCoordinateServiceTest {

    @Autowired
    private ChatCoordinateService chatCoordinateService;

    @Test
    public void testGetUserSession(){
        UserInOut userInOut = new UserInOut();
        userInOut.setUserId("1");

        User user = this.chatCoordinateService.getUserSession(userInOut);
        Assertions.assertNotNull(user);
        System.out.println("***** User not null test success ...");
        Assertions.assertNotNull(user.getSessions());
        System.out.println("***** Session not null test success ...");
        //Assertions.assertEquals(3, user.getSessions().size());
        //System.out.println("***** Session count test success with " + user.getSessions().size());
    }
}
