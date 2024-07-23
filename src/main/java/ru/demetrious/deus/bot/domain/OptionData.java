package ru.demetrious.deus.bot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OptionData {
    private String name;
    private String description;
    private Type type;

    public enum Type {
        STRING, ATTACHMENT
    }
}
