package ru.demetrious.deus.bot.app.player.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.player.TrackScheduler;

@Slf4j
@RequiredArgsConstructor
@Component
public class AudioLoadResultHandlerImpl implements AudioLoadResultHandler {
    private final TrackScheduler trackScheduler;

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        trackScheduler.queue(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        for (AudioTrack audioTrack : audioPlaylist.getTracks()) {
            trackScheduler.queue(audioTrack);
        }
    }

    @Override
    public void noMatches() {
        log.warn("Load no matches");
    }

    @Override
    public void loadFailed(FriendlyException e) {
        log.error("Load failed", e);
    }
}
