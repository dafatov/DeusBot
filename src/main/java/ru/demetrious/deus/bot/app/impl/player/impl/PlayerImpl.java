package ru.demetrious.deus.bot.app.impl.player.impl;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.adapter.duplex.jda.config.AudioSendHandler;
import ru.demetrious.deus.bot.app.api.player.ConnectOutbound;
import ru.demetrious.deus.bot.app.api.player.IsNotConnectedSameChannelOutbound;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.app.impl.player.api.Scheduler;
import ru.demetrious.deus.bot.app.impl.player.domain.Result;

import static dev.lavalink.youtube.YoutubeAudioSourceManager.SEARCH_PREFIX;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static ru.demetrious.deus.bot.app.impl.player.domain.Result.Status.IS_NOT_PLAYING;
import static ru.demetrious.deus.bot.app.impl.player.domain.Result.Status.IS_PLAYING_LIVE;
import static ru.demetrious.deus.bot.app.impl.player.domain.Result.Status.NOT_SAME_CHANNEL;
import static ru.demetrious.deus.bot.app.impl.player.domain.Result.Status.OK;
import static ru.demetrious.deus.bot.app.impl.player.domain.Result.Status.UNBOUND;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;
import static ru.demetrious.deus.bot.utils.PlayerUtils.reduceDuration;

@RequiredArgsConstructor
public class PlayerImpl implements Player {
    private final AudioPlayerManager audioPlayerManager;
    private final AudioPlayer audioPlayer;
    private final Scheduler scheduler;
    private final List<ConnectOutbound<?>> connectOutbound;
    private final List<IsNotConnectedSameChannelOutbound<?>> isNotConnectedSameChannelOutbound;

    @Override
    public void connect() {
        b(connectOutbound).connectPlayer(new AudioSendHandler(audioPlayer));
    }

    @Override
    public Optional<AudioItem> add(AudioReference reference, String userId) {
        AudioItem audioItem = audioPlayerManager.loadItemSync(reference);

        if (audioItem == null) {
            audioItem = audioPlayerManager.loadItemSync(new AudioReference(SEARCH_PREFIX + reference.getIdentifier(), reference.getTitle()));
        }

        if (audioItem instanceof AudioTrack audioTrack) {
            audioTrack.setUserData(userId);
            scheduler.enqueue(audioTrack);
        } else if (audioItem instanceof AudioPlaylist audioPlaylist) {
            if (audioPlaylist.isSearchResult()) {
                Optional<AudioTrack> audioTrack = audioPlaylist.getTracks().stream().findFirst();

                audioTrack.map(setUserId(userId)).ifPresent(scheduler::enqueue);
                return audioTrack.map(track -> track);
            }

            audioPlaylist.getTracks().stream()
                .map(setUserId(userId))
                .forEach(scheduler::enqueue);
        }

        return ofNullable(audioItem);
    }

    @Override
    public Result<List<AudioTrack>> getQueue() {
        if (isNotPlaying()) {
            return Result.of(IS_NOT_PLAYING);
        }

        return Result.of(scheduler.getQueue());
    }

    @Override
    public Long getRemaining() {
        AudioTrack playingTrack = getPlayingTrack();
        Long queueDuration = reduceDuration(getQueue().getData());

        if (playingTrack == null) {
            return queueDuration;
        }

        return queueDuration + playingTrack.getDuration() - playingTrack.getPosition();
    }

    @Override
    public Result<Void> clear() {
        return clear(false);
    }

    @Override
    public Result<Void> clear(boolean force) {
        if (!force) {
            if (isNotPlaying()) {
                return Result.of(IS_NOT_PLAYING);
            }

            if (b(isNotConnectedSameChannelOutbound).isNotConnectedSameChannel()) {
                return Result.of(NOT_SAME_CHANNEL);
            }
        }

        scheduler.clear();
        return Result.of(OK);
    }

    @Override
    public boolean isPlayingLive() {
        if (isNotPlaying()) {
            return false;
        }

        return getPlayingTrack().getInfo().isStream;
    }

    @Override
    public Result<Boolean> loop() {
        if (isNotPlaying()) {
            return Result.of(IS_NOT_PLAYING);
        }

        if (b(isNotConnectedSameChannelOutbound).isNotConnectedSameChannel()) {
            return Result.of(NOT_SAME_CHANNEL);
        }

        if (isPlayingLive()) {
            return Result.of(IS_PLAYING_LIVE);
        }

        return Result.of(scheduler.setLooped(!isLooped()));
    }

    @Override
    public boolean isLooped() {
        return scheduler.isLooped();
    }

    @Override
    public boolean isPaused() {
        return scheduler.isPaused();
    }

    @Override
    public Result<AudioTrack> move(Integer target, Integer position) {
        if (b(isNotConnectedSameChannelOutbound).isNotConnectedSameChannel()) {
            return Result.of(NOT_SAME_CHANNEL);
        }

        if (isNotValidIndex(target) || isNotValidIndex(position)) {
            return Result.of(UNBOUND);
        }

        return Result.of(scheduler.move(target, position));
    }

    @Override
    public Result<Boolean> pause() {
        return pause(false);
    }

    @Override
    public Result<Boolean> pause(boolean force) {
        if (!force) {
            if (isNotPlaying()) {
                return Result.of(IS_NOT_PLAYING);
            }

            if (b(isNotConnectedSameChannelOutbound).isNotConnectedSameChannel()) {
                return Result.of(NOT_SAME_CHANNEL);
            }

            if (isPlayingLive()) {
                return Result.of(IS_PLAYING_LIVE);
            }
        }

        boolean isPause = !audioPlayer.isPaused();

        audioPlayer.setPaused(isPause);
        return Result.of(isPause);
    }

    @Override
    public AudioTrack getPlayingTrack() {
        return audioPlayer.getPlayingTrack();
    }

    @Override
    public Result<AudioTrack> skip(boolean force) {
        if (!force) {
            if (isNotPlaying()) {
                return Result.of(IS_NOT_PLAYING);
            }

            if (b(isNotConnectedSameChannelOutbound).isNotConnectedSameChannel()) {
                return Result.of(NOT_SAME_CHANNEL);
            }
        }

        return Result.of(scheduler.next());
    }

    @Override
    public Result<AudioTrack> skip() {
        return skip(false);
    }

    @Override
    public Result<Void> shuffle() {
        if (b(isNotConnectedSameChannelOutbound).isNotConnectedSameChannel()) {
            return Result.of(NOT_SAME_CHANNEL);
        }

        if (isNotValidIndex(0)) {
            return Result.of(UNBOUND);
        }

        scheduler.shuffle();
        return Result.of(OK);
    }

    @Override
    public Result<AudioTrack> remove(Integer target) {
        if (b(isNotConnectedSameChannelOutbound).isNotConnectedSameChannel()) {
            return Result.of(NOT_SAME_CHANNEL);
        }

        if (isNotValidIndex(target)) {
            return Result.of(UNBOUND);
        }

        return Result.of(scheduler.remove(target));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Function<AudioTrack, AudioTrack> setUserId(String userId) {
        return audioTrack -> {
            audioTrack.setUserData(userId);
            return audioTrack;
        };
    }

    private boolean isNotValidIndex(Integer index) {
        return index < 0 || index >= scheduler.getQueue().size();
    }

    private boolean isNotPlaying() {
        return isNull(getPlayingTrack());
    }
}
