package ru.demetrious.deus.bot.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommandData {
    private String name;
    private String description;
    private List<OptionData> options = new ArrayList<>();
}
