package ru.demetrious.deus.bot.app.player.domain;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import java.util.List;
import lombok.Getter;

public class YoutubeAudioPlaylist extends BasicAudioPlaylist {
    @Getter
    private final String playlistId;

    /**
     * @param name           Name of the playlist
     * @param playlistId     Identifier of the playlist
     * @param tracks         List of tracks in the playlist
     * @param selectedTrack  Track that is explicitly selected
     * @param isSearchResult True if the playlist was created from search results
     */
    public YoutubeAudioPlaylist(String name, String playlistId, List<AudioTrack> tracks,
                                AudioTrack selectedTrack, boolean isSearchResult) {
        super(name, tracks, selectedTrack, isSearchResult);
        this.playlistId = playlistId;
    }
}
