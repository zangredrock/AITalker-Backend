package com.rocky.speech2textservice.service;

import com.rocky.speech2textservice.service.transcribe.TranscribeStreamingSynchronousClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
public class ConvertSpeech2TextServiceImpl implements ConvertSpeech2TextService {

    private static final Region REGION = Region.US_EAST_1;

    private static TranscribeStreamingAsyncClient client;

    @Value("${aws.transcribe.appkey}")
    private String transcribeAppkey;

    @Value("${aws.transcribe.appsecret}")
    private String transcribeAppSecret;

    @Override
    public String convertSpeech2Text(String base64Audio) {
        byte[] base64Bytes = Base64.getDecoder().decode(base64Audio);
        InputStream inputStream = new ByteArrayInputStream(base64Bytes);

        TranscribeStreamingSynchronousClient client = new TranscribeStreamingSynchronousClient(this.transcribeAppkey, this.transcribeAppSecret);
        String text = client.transcribeStream(inputStream);

        return text;
    }



}
