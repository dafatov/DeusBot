package ru.demetrious.deus.bot.app.impl.player.api;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import ru.demetrious.deus.bot.app.impl.player.domain.Result;

public interface Player {
    void connect();

    Optional<AudioItem> add(AudioReference reference, String userId);

    Result<List<AudioTrack>> getQueue();

    Long getRemaining();

    Result<Void> clear();

    Result<Void> clear(boolean force);

    boolean isPlayingLive();

    Result<Boolean> loop();

    boolean isLooped();

    boolean isPaused();

    Result<AudioTrack> move(Integer target, Integer position);

    Result<Boolean> pause();

    Result<Boolean> pause(boolean force);

    AudioTrack getPlayingTrack();

    Result<AudioTrack> skip(boolean force);

    Result<AudioTrack> skip();

    Result<Void> shuffle();

    Result<AudioTrack> remove(Integer target);
}
