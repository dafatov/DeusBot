package ru.demetrious.deus.bot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import static ru.demetrious.deus.bot.domain.TextInputComponent.StyleEnum.SHORT;

@Data
@Accessors(chain = true)
public class TextInputComponent {
    private String id;
    private String label;
    private StyleEnum style = SHORT;
    private String placeholder;
    private boolean isRequired;

    public enum StyleEnum {
        SHORT
    }
}
