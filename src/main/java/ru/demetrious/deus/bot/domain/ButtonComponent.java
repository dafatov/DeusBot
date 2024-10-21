package ru.demetrious.deus.bot.domain;

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
    private EmojiEnum emoji;
    private boolean isDisabled = false;

    public enum StyleEnum {
        SECONDARY, DANGER
    }

    @Getter
    @RequiredArgsConstructor
    public enum EmojiEnum {
        CLEAN("<:clean:1266035815528792134>"),
        CLEAR("<:clear:1266035817042940065>"),
        FIRST("<:first:1266035818628255859>"),
        LAST("<:last:1266035820054450196>"),
        NEXT("<:next:1266035820985454724>"),
        PAUSE("<:pause:1266035822986268734>"),
        PLAY("<:play:1266035825238605845>"),
        PREVIOUS("<:previous:1266035826555359242>"),
        QUEUE("<:queue:1266035828174622730>"),
        REFRESH("<:refresh:1266035829634236518>"),
        REPEAT("<:repeat:1266035894180249601>"),
        SHUFFLE("<:shuffle:1266035834105364533>"),
        SKIP("<:skip:1266035837787967633>");

        private final String value;
    }
}
