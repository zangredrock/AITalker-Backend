package com.rocky.coordinatorservice.persistence;

import com.rocky.coordinatorservice.bean.UserInOut;
import com.rocky.coordinatorservice.model.AIMessage;
import com.rocky.coordinatorservice.model.SessionMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Component
public class DynamoDBAccess {

    private DynamoDbClient dynamoDbClient;

    private static final String TABLE_NAME = "AITalkerMessages";

    public DynamoDBAccess(@Value("${aws.dynamodb.region}") String region,
                          @Value("${aws.dynamodb.appkey}") String appkey,
                          @Value("${aws.dynamodb.appsecret}") String appSecret) {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(appkey, appSecret)))
                .build();
    }

    public SessionMessage getSessionMessage(UserInOut userInOut) {
        SessionMessage sessionMessage = new SessionMessage();
        sessionMessage.setSessionId(userInOut.getSessionId());
        sessionMessage.setUserId(userInOut.getUserId());

        this.getDynamoDBItem(sessionMessage);

        return sessionMessage;
    }

    private void getDynamoDBItem(SessionMessage sessionMessage) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("userId", AttributeValue.builder().s(sessionMessage.getUserId()).build());
        keyToGet.put("sessionId", AttributeValue.builder().s(sessionMessage.getSessionId()).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(TABLE_NAME)
                .build();

        try {
            // If there is no matching item, GetItem does not return any data.
            Map<String, AttributeValue> returnedItem = this.dynamoDbClient.getItem(request).item();
            if (returnedItem.isEmpty())
                System.out.format("No item found with the userId: " + sessionMessage.getUserId() + ", sessionId: " + sessionMessage.getSessionId());
            else {
                AttributeValue val = returnedItem.get("messages");
                if (val != null) {
                    List<AIMessage> aiMessageList = new ArrayList<>();
                    List<AttributeValue> messageList = val.l();

                    for (AttributeValue message : messageList) {
                        AIMessage aiMessage = new AIMessage();
                        Map<String, AttributeValue> messageMap = message.m();
                        aiMessage.setRole(messageMap.get("role").s());
                        aiMessage.setContent(messageMap.get("content").s());
                        aiMessageList.add(aiMessage);
                    }
                    sessionMessage.setMessages(aiMessageList);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    public void saveItem(SessionMessage sessionMessage) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("userId", AttributeValue.builder().s(sessionMessage.getUserId()).build());
        itemValues.put("sessionId", AttributeValue.builder().s(sessionMessage.getSessionId()).build());

        List<AttributeValue> messageList = new ArrayList<>();
        for(AIMessage aiMessage : sessionMessage.getMessages()) {
            Map<String, AttributeValue> messageMap = new HashMap<>();
            messageMap.put("role", AttributeValue.builder().s(aiMessage.getRole()).build());
            messageMap.put("content", AttributeValue.builder().s(aiMessage.getContent()).build());
            messageList.add(AttributeValue.builder().m(messageMap).build());
        }
        itemValues.put("messages", AttributeValue.builder().l(messageList).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();
        try {
            PutItemResponse response = this.dynamoDbClient.putItem(request);
            System.out.println(TABLE_NAME + " was successfully updated. The request id is "
                    + response.responseMetadata().requestId());

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", TABLE_NAME);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }

    }

    //for update item, can also refer to https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.UpdateItem.html
    public void updateItem(SessionMessage sessionMessage) {
        HashMap<String, AttributeValue> keyValues = new HashMap<>();
        keyValues.put("userId", AttributeValue.builder().s(sessionMessage.getUserId()).build());
        keyValues.put("sessionId", AttributeValue.builder().s(sessionMessage.getSessionId()).build());

        HashMap<String, AttributeValue> updateValues = new HashMap<>();
        List<AttributeValue> messageList = new ArrayList<>();
        for(AIMessage aiMessage : sessionMessage.getMessages()) {
            Map<String, AttributeValue> messageMap = new HashMap<>();
            messageMap.put("role", AttributeValue.builder().s(aiMessage.getRole()).build());
            messageMap.put("content", AttributeValue.builder().s(aiMessage.getContent()).build());
            messageList.add(AttributeValue.builder().m(messageMap).build());
        }
        updateValues.put(":messages", AttributeValue.builder().l(messageList).build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(keyValues)
                .updateExpression("set messages = :messages")
                .expressionAttributeValues(updateValues)
                .build();
        try {
            this.dynamoDbClient.updateItem(request);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


}
