package com.rocky.speech2textservice.service.transcribe;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.services.transcribestreaming.model.AudioStream;

import java.io.InputStream;

/**
 * AudioStreamPublisher implements audio stream publisher.
 * AudioStreamPublisher emits audio stream asynchronously in a separate thread
 */
public class AudioStreamPublisher implements Publisher<AudioStream> {

    private final InputStream inputStream;

    public AudioStreamPublisher(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void subscribe(Subscriber<? super AudioStream> s) {
        s.onSubscribe(new ByteToAudioEventSubscription(s, inputStream));
    }
}
