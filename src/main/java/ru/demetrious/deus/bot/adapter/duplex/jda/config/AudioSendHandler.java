package ru.demetrious.deus.bot.adapter.duplex.jda.config;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import java.nio.ByteBuffer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;

@RequiredArgsConstructor
@Component
public class AudioSendHandler implements net.dv8tion.jda.api.audio.AudioSendHandler {
    @Getter
    private final boolean opus = true;
    private final MutableAudioFrame mutableAudioFrame = new MutableAudioFrame(allocate(3840));

    private final AudioPlayer audioPlayer;

    @Override
    public boolean canProvide() {
        return audioPlayer.provide(mutableAudioFrame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return wrap(mutableAudioFrame.getData());
    }
}
