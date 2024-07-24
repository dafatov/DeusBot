package ru.demetrious.deus.bot.app.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
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
    public void connect(GenericInteractionAdapter<?> genericInteractionAdapter) {
        VoiceChannel voiceChannel = genericInteractionAdapter.getVoiceChannel();
        AudioManager audioManager = genericInteractionAdapter.getAudioManager();

        if (audioManager.isConnected()) {
            return;
        }

        audioManager.setSendingHandler(new AudioSendHandlerImpl(audioPlayer));
        audioManager.openAudioConnection(voiceChannel);
    }

    @Override
    public Optional<AudioItem> add(AudioReference reference) {
        AudioItem audioItem = audioPlayerManager.loadItemSync(reference);

        if (audioItem == null) {
            audioItem = audioPlayerManager.loadItemSync(new AudioReference(SEARCH_PREFIX + reference.getIdentifier(), reference.getTitle()));
        }

        if (audioItem instanceof AudioTrack audioTrack) {
            scheduler.enqueue(audioTrack);
        } else if (audioItem instanceof AudioPlaylist audioPlaylist) {
            if (audioPlaylist.isSearchResult()) {
                Optional<AudioTrack> audioTrack = audioPlaylist.getTracks().stream().findFirst();

                audioTrack.ifPresent(scheduler::enqueue);
                return audioTrack.map(track -> track);
            }

            audioPlaylist.getTracks().forEach(scheduler::enqueue);
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
        return getPlayingTrack().getInfo().isStream;
    }

    @Override
    public boolean loop() {
        return scheduler.setLoop(!scheduler.getLoop());
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private AudioTrack getPlayingTrack() {
        return audioPlayer.getPlayingTrack();
    }
}
