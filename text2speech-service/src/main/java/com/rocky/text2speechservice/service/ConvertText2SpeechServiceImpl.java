package com.rocky.text2speechservice.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsSyncClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

@Service
public class ConvertText2SpeechServiceImpl implements ConvertText2SpeechService {

    private final AmazonPolly polly;

    private String voiceId;

    public ConvertText2SpeechServiceImpl(@Value("${aws.polly.appkey}") String appKey, @Value("${aws.polly.appsecret}") String appSecret) {
        // create an Amazon Polly client in a specific region
        AWSCredentials credentials = new BasicAWSCredentials(appKey, appSecret);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        this.polly = AmazonPollyClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .withClientConfiguration(clientConfiguration).build();

        // Create describe voices request.
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

        // Synchronously ask Amazon Polly to describe available TTS voices.
        DescribeVoicesResult describeVoicesResult = polly.describeVoices(describeVoicesRequest);
        this.voiceId = "Ivy";//describeVoicesResult.getVoices().get(0).getId();
        for (Voice voice : describeVoicesResult.getVoices()) {
            if (voice.getGender().equals("Female")) {
                System.out.println(voice.getId() + " : " + voice.getName() + " : " + voice.getGender() + " : " + voice.getLanguageName());
            }
        }

    }

    @Override
    public byte[] convertText2Speech(String text) {
        byte[] result = null;
        InputStream audioStream = null;
        try {
            audioStream = this.synthesize(text);
            result = audioStream.readAllBytes();

            //this.writeAudioToFile(result);
        } catch (IOException e) {
            System.out.println("Failed to get audio stream: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (audioStream != null) {
                    audioStream.close();
                }
            } catch (IOException e) {
                System.out.println("Failed to close input stream: " + e.getMessage());
            }
        }

        return result;
    }

    private InputStream synthesize(String text) throws IOException {
        SynthesizeSpeechRequest synthReq =
                new SynthesizeSpeechRequest().withText(text).withVoiceId(this.voiceId)
                        .withOutputFormat(OutputFormat.Mp3).withEngine(Engine.Neural)
                        .withSampleRate("16000").withLanguageCode(LanguageCode.EnUS);
        SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);

        return synthRes.getAudioStream();
    }

    private void writeAudioToFile(byte[] audioBytes) {
        OutputStream outputStream = null;
        try {
            File destinationFile = new File("/Users/rocky/Downloads/javatest.pcm");
            outputStream = new FileOutputStream(destinationFile);
            outputStream.write(audioBytes);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
