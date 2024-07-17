package com.rocky.aichatservice.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.rocky.aichatservice.bean.AIMessage;
import com.rocky.aichatservice.bean.SessionMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIChatServiceImpl implements AIChatService{

    @Value("${azure.openai.appkey}")
    private String azureOpenAIAppKey;

    @Value("${azure.openai.url}")
    private String azureOpenAIUrl;

    @Value("${azure.openai.model}")
    private String azureOpenAIModel;

    private OpenAIClient client;

    private static final String CHATGPT_SYSTEM_MESSAGE = "You are a helpful Canadian secondary schoole student who is willing to communicate and talk in English with non-native speakers";

    public String chat(SessionMessage session) {
        if (this.client == null) {
            this.client = new OpenAIClientBuilder()
                    .credential(new AzureKeyCredential(this.azureOpenAIAppKey))
                    .endpoint(this.azureOpenAIUrl)
                    .buildClient();
        }

        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage(CHATGPT_SYSTEM_MESSAGE));
        session.getMessages().forEach(message -> {
            if ("user".equals(message.getRole())) {
                chatMessages.add(new ChatRequestUserMessage(message.getContent()));
            } else if ("assistant".equals(message.getRole())) {
                chatMessages.add(new ChatRequestAssistantMessage(message.getContent()));
            }
        });

        ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
        options.setTemperature((double) 0);
        options.setMaxTokens(200);

        ChatCompletions chatCompletions = this.client.getChatCompletions(this.azureOpenAIModel, options);

        System.out.printf("Model ID=%s is created at %s.%n", chatCompletions.getId(), chatCompletions.getCreatedAt());

        String responseText = "Something went wrong ...";
        if (chatCompletions.getChoices() != null && !chatCompletions.getChoices().isEmpty()) {
            System.out.println("chat completion choices size is " + chatCompletions.getChoices().size());

            for (ChatChoice choice : chatCompletions.getChoices()) {
                ChatResponseMessage response = choice.getMessage();
                System.out.printf("Index: %d, Chat Role: %s.%n", choice.getIndex(), response.getRole());
                System.out.println("Message:");
                System.out.println(response.getContent());
                responseText = response.getContent();
            }
        }

        CompletionsUsage usage = chatCompletions.getUsage();
        System.out.printf("Usage: number of prompt token is %d, "
                        + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());

        return responseText;
    }

    @Override
    public String chat(String message) {
        SessionMessage session = new SessionMessage();
        List<AIMessage> messageList = new ArrayList<>();
        AIMessage aiMessage = new AIMessage();
        aiMessage.setContent(message);
        messageList.add(aiMessage);
        session.setMessages(messageList);

        return this.chat(session);
    }

    public String generateImage(String message) {
        if (this.client == null) {
            this.client = new OpenAIClientBuilder()
                    .credential(new AzureKeyCredential(this.azureOpenAIAppKey))
                    .endpoint(this.azureOpenAIUrl)
                    .buildClient();
        }

        ImageGenerationOptions imageGenerationOptions = new ImageGenerationOptions(message);
        ImageGenerations images = this.client.getImageGenerations(this.azureOpenAIModel, imageGenerationOptions);

        for (ImageGenerationData imageGenerationData : images.getData()) {
            String url = imageGenerationData.getUrl();
            System.out.printf( "Image location URL that provides temporary access to download the generated image is %s.%n", url);
            return url;
        }

        return "Nothing to generate";
    }

}
