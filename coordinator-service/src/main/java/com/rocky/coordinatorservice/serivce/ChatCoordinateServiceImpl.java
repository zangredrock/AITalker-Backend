package com.rocky.coordinatorservice.serivce;

import com.rocky.coordinatorservice.bean.UserInOut;
import com.rocky.coordinatorservice.client.AiChatServiceClient;
import com.rocky.coordinatorservice.client.Speech2TextServiceClient;
import com.rocky.coordinatorservice.client.Text2SpeechServiceClient;
import com.rocky.coordinatorservice.mapper.UserSessionMapper;
import com.rocky.coordinatorservice.model.AIMessage;
import com.rocky.coordinatorservice.model.Session;
import com.rocky.coordinatorservice.model.SessionMessage;
import com.rocky.coordinatorservice.model.User;
import com.rocky.coordinatorservice.persistence.DynamoDBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatCoordinateServiceImpl implements ChatCoordinateService{

    @Autowired
    private AiChatServiceClient aiChatServiceClient;

    @Autowired
    private Speech2TextServiceClient speech2TextServiceClient;

    @Autowired
    private Text2SpeechServiceClient text2SpeechServiceClient;

    @Autowired
    private DynamoDBAccess dynamoDBAccess;

    @Autowired
    private UserSessionMapper userSessionMapper;

    private static final String CHATGPT_SYSTEM_CONTENT = "You are a helpful Canadian secondary schoole student " +
            "who is willing to communicate and talk in English with non-native speakers";

    public UserInOut coordinateChat(UserInOut input) {
        String base64Audio = "";

        //step 1: get text message based on user audio data
        String text = this.speech2TextServiceClient.convertSpeech2Text(input);

        //setp 2: retrieve user session info from dynamoDB, and compose into ChatGPT format
        SessionMessage sessionMessage = this.dynamoDBAccess.getSessionMessage(input);
        if (sessionMessage.getMessages() == null || sessionMessage.getMessages().size() == 0) {
            System.err.println("************** No session messages found in DynamoDB ...");
            sessionMessage = this.createSession(input);
        }
        AIMessage newMessage = new AIMessage();
        newMessage.setRole("user");
        newMessage.setContent(text);
        sessionMessage.getMessages().add(newMessage);

        //step 3: get chat response from Azure OpeAI
        String response = this.aiChatServiceClient.chatback(sessionMessage);
        AIMessage responseMessage = new AIMessage();
        responseMessage.setRole("assistant");
        responseMessage.setContent(response);
        sessionMessage.getMessages().add(responseMessage);
        input.setText(response);

        //step 4: save session messages into DynamoDB
        //TODO: change to use Queue to decouple later
        this.dynamoDBAccess.updateItem(sessionMessage);

        //step 5: convert OpenAI response into audio data
        UserInOut output = new UserInOut();
        output.setText(response);
        output = this.text2SpeechServiceClient.convertText2Speech(output);
        input.setAudio(output.getAudio());
        input.setSessionId(sessionMessage.getSessionId());

        return input;
    }

    public User getUserSession(UserInOut input) {
        return this.userSessionMapper.findUserSession(Integer.valueOf(input.getUserId()));
    }

    public SessionMessage createSession(UserInOut input) {
        //save session info into MySQL DB
        Session session = new Session();
        session.setUserId(Integer.valueOf(input.getUserId()));
        session.setName(input.getSessionName());
        this.userSessionMapper.saveSession(session);

        //get session id by using session name and user id
        session = this.userSessionMapper.getSessionByName(input.getSessionName(), Integer.valueOf(input.getUserId()));

        //save session and messages info into DynamoDB
        SessionMessage sessionMessage = new SessionMessage();
        sessionMessage.setSessionId(session.getId().toString());
        sessionMessage.setUserId(session.getUserId().toString());

        List<AIMessage> aiMessages = new ArrayList<>();
        AIMessage aiMessage = new AIMessage();
        aiMessage.setRole("assistant");
        aiMessage.setContent(CHATGPT_SYSTEM_CONTENT);
        aiMessages.add(aiMessage);
        sessionMessage.setMessages(aiMessages);

        this.dynamoDBAccess.saveItem(sessionMessage);

        return sessionMessage;
    }
}
