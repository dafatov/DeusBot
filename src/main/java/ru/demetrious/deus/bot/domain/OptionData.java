package ru.demetrious.deus.bot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OptionData {
    private String name;
    private String description;
    private Type type;
    private boolean isRequired = false;

    public enum Type {
        STRING, INTEGER, ATTACHMENT
    }
}
