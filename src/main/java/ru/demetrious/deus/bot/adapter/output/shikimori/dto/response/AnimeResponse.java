package ru.demetrious.deus.bot.adapter.output.shikimori.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.ResponseSerialize;

import static java.util.Arrays.stream;

@Data
public class AnimeResponse implements ResponseSerialize {
    private IncompleteDate airedOn;
    private String franchise;
    private Set<Genre> genres;
    private String japanese;
    private String name;
    private Origin origin;
    private String russian;
    private double score;
    private Set<Studio> studios;
    private Set<String> synonyms;

    @Getter
    @RequiredArgsConstructor
    public enum Origin {
        ORIGINAL("original"),
        MANGA("manga"),
        WEB_MANGA("web_manga"),
        YONKOMA_MANGA("four_koma_manga"),
        NOVEL("novel"),
        WEB_NOVEL("web_novel"),
        VISUAL_NOVEL("visual_novel"),
        LIGHT_NOVEL("light_novel"),
        GAME("game"),
        CARD_GAME("card_game"),
        MUSIC("music"),
        RADIO("radio"),
        BOOK("book"),
        PICTURE_BOOK("picture_book"),
        MIXED_MEDIA("mixed_media"),
        OTHER("other"),
        UNKNOWN("unknown");

        private final String value;

        @JsonCreator
        public static Origin fromValue(String value) {
            return stream(values())
                .filter(v -> v.getValue().equals(value))
                .findFirst()
                .orElseThrow();
        }
    }

    @Data
    public static class IncompleteDate {
        private int year;
    }

    @Data
    public static class Genre {
        private Kind kind;
        private String russian;

        @Getter
        @RequiredArgsConstructor
        public enum Kind {
            DEMOGRAPHIC("demographic"),
            GENRE("genre"),
            THEME("theme");

            private final String value;

            @JsonCreator
            public static Kind fromValue(String value) {
                return stream(values())
                    .filter(v -> v.getValue().equals(value))
                    .findFirst()
                    .orElseThrow();
            }
        }
    }

    @Data
    public static class Studio {
        private String name;
    }
}
