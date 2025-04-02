package ru.demetrious.deus.bot.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.Arrays.stream;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Franchise {
    @EqualsAndHashCode.Include
    private String name;
    private String firstTitle;
    private String firstUrl;
    private Set<String> genres;
    private Set<String> themes;
    private Set<String> titles;
    private int minAiredOnYear;
    private double averageScore;
    private Set<String> studios;
    private Set<Source> sources;
    private int episodes;
    private long averageDuration;

    @Getter
    @RequiredArgsConstructor
    public enum Source {
        ORIGINAL("original", "Оригинальное"),
        MANGA("manga", "Манга"),
        WEB_MANGA("web_manga", "Веб манга"),
        YONKOMA_MANGA("four_koma_manga", "Ёнкома"),
        NOVEL("novel", "Новелла"),
        WEB_NOVEL("web_novel", "Веб новелла"),
        VISUAL_NOVEL("visual_novel", "Визуальная новелла"),
        LIGHT_NOVEL("light_novel", "Легкая новелла"),
        GAME("game", "Игра"),
        CARD_GAME("card_game", "Карточная игра"),
        MUSIC("music", "Музыка"),
        RADIO("radio", "Радио"),
        BOOK("book", "Книга"),
        PICTURE_BOOK("picture_book", "Книга-картина"),
        MIXED_MEDIA("mixed_media", "Смешанные медиа"),
        OTHER("other", "Иное"),
        UNKNOWN("unknown", "Неизвестно");

        private final String value;
        private final String localized;

        @JsonCreator
        public static Source fromValue(String value) {
            return stream(values())
                .filter(v -> v.getValue().equals(value))
                .findFirst()
                .orElseThrow();
        }
    }
}
