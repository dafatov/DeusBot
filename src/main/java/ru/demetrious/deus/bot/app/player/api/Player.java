package ru.demetrious.deus.bot.app.player.api;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;

public interface Player {
    void connect(GenericInteractionAdapter<?> genericInteractionAdapter);

    Optional<AudioItem> add(AudioReference reference);

    List<AudioTrack> getQueue();

    Long getRemaining();

    void clear();

    boolean isNotPlaying();

    boolean isPlayingLive();

    boolean loop();

    boolean isValidIndex(Integer index);

    AudioTrack move(Integer target, Integer position);
}
