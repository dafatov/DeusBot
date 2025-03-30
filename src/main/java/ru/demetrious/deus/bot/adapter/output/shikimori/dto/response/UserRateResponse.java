package ru.demetrious.deus.bot.adapter.output.shikimori.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.Instant;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.domain.graphql.ResponseSerialize;

import static java.util.Arrays.stream;

@Data
public class UserRateResponse implements ResponseSerialize {
    private Anime anime;
    private Integer episodes;
    private Integer rewatches;
    private Integer score;
    private Status status;
    private String text;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        PLANNED("planned"),
        WATCHING("watching"),
        REWATCHING("rewatching"),
        COMPLETED("completed"),
        ON_HOLD("on_hold"),
        DROPPED("dropped");

        private final String value;

        @JsonCreator
        public static Status fromValue(String value) {
            return stream(values())
                .filter(v -> v.getValue().equals(value))
                .findFirst()
                .orElseThrow();
        }
    }

    @Data
    public static class Anime {
        private String name;
        private String kind;
        private Integer episodes;
        private Integer malId;
    }
}
