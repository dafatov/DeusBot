package ru.demetrious.deus.bot.domain.freebie;

import java.time.Instant;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import static java.util.Arrays.stream;

@Data
@Accessors(chain = true)
public class FreebieItem {
    private String link;
    private String description;
    private String title;
    private Category category;
    private Instant pubDate;

    @RequiredArgsConstructor
    public enum Category {
        EPIC_GAMES("Epic Games", "https://img.icons8.com/nolan/512/epic-games.png"),
        GOG("GOG", "https://i.imgur.com/BLhrDmX.png"),
        STEAM("Steam", "https://workinnet.ru/wp-content/uploads/2022/06/steam_logo-1024x1024.png");

        private final String value;
        @Getter
        private final String thumbnail;

        public static Category fromValue(String value) {
            return stream(values())
                .filter(c -> StringUtils.equals(value, c.value))
                .findFirst()
                .orElse(null);
        }
    }
}
