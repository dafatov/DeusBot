package ru.demetrious.deus.bot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Anime {
    private String title;
    private String type;
    private Integer episodes;
    private Integer animedbId;
    private Integer watchedEpisodes;
    private Integer rewatched;
    private Integer score;
    private Status status;
    private String comment;

    public enum Status {
        COMPLETED,
        WATCHING,
        DROPPED,
        PAUSED,
        PLANNED,
        REPEATING
    }
}
