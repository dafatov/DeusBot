package ru.demetrious.deus.bot.app.player;

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
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.handler.AudioSendHandlerImpl;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.app.player.api.Scheduler;

import static dev.lavalink.youtube.YoutubeAudioSourceManager.SEARCH_PREFIX;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static ru.demetrious.deus.bot.fw.utils.PlayerUtils.reduceDuration;

@RequiredArgsConstructor
@Component
public class PlayerImpl implements Player {
    private final AudioPlayerManager audioPlayerManager;
    private final AudioPlayer audioPlayer;
    private final Scheduler scheduler;

    @Override
    public void connect(GenericInteractionAdapter genericInteractionAdapter) {
        genericInteractionAdapter.connectPlayer(new AudioSendHandlerImpl(audioPlayer));
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
    public List<AudioTrack> getQueue() {
        return scheduler.getQueue();
    }

    @Override
    public Long getRemaining() {
        AudioTrack playingTrack = getPlayingTrack();
        Long queueDuration = reduceDuration(getQueue());

        if (playingTrack == null) {
            return queueDuration;
        }

        return queueDuration + playingTrack.getDuration() - playingTrack.getPosition();
    }

    @Override
    public void clear() {
        scheduler.clear();
    }

    @Override
    public boolean isNotPlaying() {
        return isNull(getPlayingTrack());
    }

    @Override
    public boolean isPlayingLive() {
        if (isNotPlaying()) {
            return false;
        }

        return getPlayingTrack().getInfo().isStream;
    }

    @Override
    public boolean loop() {
        return scheduler.setLooped(!isLooped());
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
    public boolean isNotValidIndex(Integer index) {
        return !isValidIndex(index);
    }

    @Override
    public boolean isValidIndex(Integer index) {
        return index >= 0 && index < scheduler.getQueue().size();
    }

    @Override
    public AudioTrack move(Integer target, Integer position) {
        return scheduler.move(target, position);
    }

    @Override
    public boolean pause() {
        boolean isPause = !audioPlayer.isPaused();

        audioPlayer.setPaused(isPause);
        return isPause;
    }

    @Override
    public AudioTrack getPlayingTrack() {
        return audioPlayer.getPlayingTrack();
    }

    @Override
    public void skip() {
        scheduler.next();
    }

    @Override
    public void shuffle() {
        scheduler.shuffle();
    }

    @Override
    public AudioTrack remove(Integer target) {
        return scheduler.remove(target);
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
}
