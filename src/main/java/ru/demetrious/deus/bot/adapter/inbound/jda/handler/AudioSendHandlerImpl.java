package ru.demetrious.deus.bot.adapter.inbound.jda.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AudioSendHandlerImpl implements AudioSendHandler {
    private final AudioPlayer audioPlayer;

    private AudioFrame lastFrame;

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();

        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
