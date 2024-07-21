package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import ru.demetrious.deus.bot.domain.MessageData;

public interface CommandAdapter {
    void notify(String content);

    void notify(MessageData content);

    String getLatency();

    void connectPlayer();

    List<AudioTrack> getQueue();
}
