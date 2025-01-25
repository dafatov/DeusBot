package ru.demetrious.deus.bot.domain;

import java.nio.file.Path;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static ru.demetrious.deus.bot.domain.ButtonComponent.StyleEnum.SECONDARY;

@Data
@Accessors(chain = true)
public class ButtonComponent {
    private StyleEnum style = SECONDARY;
    private String id;
    private String label;
    private EmojiEnum emoji;
    private boolean isDisabled = false;

    public enum StyleEnum {
        DANGER, LINK, SECONDARY, SUCCESS
    }

    @Getter
    @RequiredArgsConstructor
    public enum EmojiEnum {
        CLEAN("clean", Path.of("discord/emoji/clean.png")),
        CLEAR("clear", Path.of("discord/emoji/clear.png")),
        FIRST("first", Path.of("discord/emoji/first.png")),
        LAST("last", Path.of("discord/emoji/last.png")),
        NEXT("next", Path.of("discord/emoji/next.png")),
        PAUSE("pause", Path.of("discord/emoji/pause.png")),
        PLAY("play", Path.of("discord/emoji/play.png")),
        PREVIOUS("previous", Path.of("discord/emoji/previous.png")),
        REFRESH("refresh", Path.of("discord/emoji/refresh.png")),
        REPEAT("repeat", Path.of("discord/emoji/repeat.png")),
        SHUFFLE("shuffle", Path.of("discord/emoji/shuffle.png")),
        SKIP("skip", Path.of("discord/emoji/skip.png"));

        private final String name;
        private final Path icon;
    }
}
