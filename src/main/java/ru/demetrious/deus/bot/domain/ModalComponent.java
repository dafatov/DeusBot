package ru.demetrious.deus.bot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ModalComponent {
    private String label;
    private TextInputComponent textInput;
}
