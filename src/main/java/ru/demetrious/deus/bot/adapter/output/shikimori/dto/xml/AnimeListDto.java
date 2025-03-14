package ru.demetrious.deus.bot.adapter.output.shikimori.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JacksonXmlRootElement(localName = "myanimelist")
public class AnimeListDto {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "anime")
    private List<Anime> animeList;

    @Data
    public static class Anime {
        @JacksonXmlProperty(localName = "series_title")
        private String name;
        @JacksonXmlProperty(localName = "series_type")
        private String kind;
        @JacksonXmlProperty(localName = "series_episodes")
        private Integer episodes;
        @JacksonXmlProperty(localName = "series_animedb_id")
        private Integer malId;
        @JacksonXmlProperty(localName = "my_watched_episodes")
        private Integer watchedEpisodes;
        @JacksonXmlProperty(localName = "my_times_watched")
        private Integer rewatches;
        @JacksonXmlProperty(localName = "my_score")
        private Integer score;
        @JacksonXmlProperty(localName = "my_status")
        private String status;
        @JacksonXmlProperty(localName = "shiki_status")
        private String shikiStatus;
        @JacksonXmlProperty(localName = "my_comments")
        private String text;
        @JacksonXmlProperty(localName = "my_start_date")
        private String createdAt;
        @JacksonXmlProperty(localName = "my_finish_date")
        private String updatedAt;
    }
}
