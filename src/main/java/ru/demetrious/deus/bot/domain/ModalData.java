package ru.demetrious.deus.bot.domain;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ModalData {
    private String customId;
    private String title;
    private List<ModalComponent> components;
}
