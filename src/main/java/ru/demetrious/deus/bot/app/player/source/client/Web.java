package ru.demetrious.deus.bot.app.player.source.client;

import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.demetrious.deus.bot.app.player.domain.YoutubeAudioPlaylist;

public class Web extends dev.lavalink.youtube.clients.Web {
    @Override
    public AudioItem loadPlaylist(@NotNull YoutubeAudioSourceManager source, @NotNull HttpInterface httpInterface, @NotNull String playlistId,
                                  @Nullable String selectedVideoId) {
        BasicAudioPlaylist basicAudioPlaylist = ((BasicAudioPlaylist) super.loadPlaylist(source, httpInterface, playlistId, selectedVideoId));

        assert basicAudioPlaylist != null;
        return new YoutubeAudioPlaylist(basicAudioPlaylist.getName(), playlistId,
            basicAudioPlaylist.getTracks(), basicAudioPlaylist.getSelectedTrack(), basicAudioPlaylist.isSearchResult());
    }
}
