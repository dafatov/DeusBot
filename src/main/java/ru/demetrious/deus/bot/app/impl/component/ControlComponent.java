package ru.demetrious.deus.bot.app.impl.component;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.domain.ButtonComponent;
import ru.demetrious.deus.bot.domain.ButtonComponent.EmojiEnum;
import ru.demetrious.deus.bot.domain.MessageComponent;

import static ru.demetrious.deus.bot.domain.ButtonComponent.StyleEnum.DANGER;

@RequiredArgsConstructor
public class ControlComponent implements Component {
    private static final String EASTER_EGG_USER_ID = "217196050337890305";

    private final Player player;
    private final String authorId;

    @Override
    public MessageComponent get() {
        return new MessageComponent().setButtons(List.of(
            new ButtonComponent()
                .setStyle(DANGER)
                .setId(ButtonIdEnum.CLEAR.name())
                .setEmoji(EASTER_EGG_USER_ID.equals(authorId) ? EmojiEnum.CLEAN : EmojiEnum.CLEAR),
            new ButtonComponent()
                .setId(ButtonIdEnum.REPEAT.name())
                .setEmoji(player.isLooped() ? EmojiEnum.REPEAT : EmojiEnum.QUEUE)
                .setDisabled(player.isPlayingLive()),
            new ButtonComponent()
                .setId(ButtonIdEnum.SKIP.name())
                .setEmoji(EmojiEnum.SKIP),
            new ButtonComponent()
                .setId(ButtonIdEnum.PAUSE.name())
                .setEmoji(player.isPaused() ? EmojiEnum.PAUSE : EmojiEnum.PLAY)
                .setDisabled(player.isPlayingLive()),
            new ButtonComponent()
                .setId(ButtonIdEnum.SHUFFLE.name())
                .setEmoji(EmojiEnum.SHUFFLE)
        ));
    }

    @Override
    public MessageComponent update(String customId) {
        if (Arrays.stream(ButtonIdEnum.values()).map(Enum::name).toList().contains(customId)) {
            switch (ButtonIdEnum.from(customId)) {
                case CLEAR -> player.clear();
                case REPEAT -> player.loop();
                case SKIP -> player.skip();
                case PAUSE -> player.pause();
                case SHUFFLE -> player.shuffle();
            }
        }

        return get();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private enum ButtonIdEnum {
        CLEAR, REPEAT, SKIP, PAUSE, SHUFFLE;

        public static ButtonIdEnum from(String buttonId) {
            return Enum.valueOf(ButtonIdEnum.class, buttonId);
        }
    }
}
