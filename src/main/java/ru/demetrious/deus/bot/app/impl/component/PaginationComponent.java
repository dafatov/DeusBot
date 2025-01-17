package ru.demetrious.deus.bot.app.impl.component;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.domain.ButtonComponent;
import ru.demetrious.deus.bot.domain.ButtonComponent.EmojiEnum;
import ru.demetrious.deus.bot.domain.MessageComponent;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

@RequiredArgsConstructor
public class PaginationComponent implements Component {
    private final int dataLength;
    @Getter
    private int start = 0;
    @Getter
    private int count = 5;

    public static PaginationComponent from(String footer, int dataLength) {
        String[] split = footer.split(" ");
        PaginationComponent paginationComponent = new PaginationComponent(dataLength);

        paginationComponent.start = max(parseInt(split[0]), 1) - 1;
        paginationComponent.count = parseInt(split[6]);

        return paginationComponent;
    }

    @Override
    public MessageComponent get() {
        return new MessageComponent().setButtons(List.of(
            new ButtonComponent()
                .setId(ButtonIdEnum.FIRST.name())
                .setEmoji(EmojiEnum.FIRST)
                .setDisabled(start <= 0),
            new ButtonComponent()
                .setId(ButtonIdEnum.PREVIOUS.name())
                .setEmoji(EmojiEnum.PREVIOUS)
                .setDisabled(start <= 0),
            new ButtonComponent()
                .setId(ButtonIdEnum.REFRESH.name())
                .setEmoji(EmojiEnum.REFRESH),
            new ButtonComponent()
                .setId(ButtonIdEnum.NEXT.name())
                .setEmoji(EmojiEnum.NEXT)
                .setDisabled(start + count >= dataLength),
            new ButtonComponent()
                .setId(ButtonIdEnum.LAST.name())
                .setEmoji(EmojiEnum.LAST)
                .setDisabled(start + count >= dataLength)
        ));
    }

    @Override
    public MessageComponent update(String customId) {
        if (stream(ButtonIdEnum.values()).map(Enum::name).toList().contains(customId)) {
            switch (ButtonIdEnum.from(customId)) {
                case FIRST -> start = 0;
                case PREVIOUS -> start -= count;
                case REFRESH -> start = min(start, count * (max(0, dataLength - 1) / count));
                case NEXT -> start += count;
                case LAST -> start = count * ((dataLength - 1) / count);
            }
        }

        return get();
    }

    public String getFooter() {
        return format("{0} - {1} из {2} по {3}",
            min(start + 1, dataLength),
            min(start + count, dataLength),
            dataLength,
            count
        );
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private enum ButtonIdEnum {
        FIRST, PREVIOUS, REFRESH, NEXT, LAST;

        public static ButtonIdEnum from(String buttonId) throws IllegalArgumentException {
            return Enum.valueOf(ButtonIdEnum.class, buttonId);
        }
    }
}
