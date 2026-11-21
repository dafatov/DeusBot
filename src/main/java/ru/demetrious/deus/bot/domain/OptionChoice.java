package ru.demetrious.deus.bot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

public interface OptionChoice {
    String getName();

    String getValue();

    @Data
    @Accessors(chain = true)
    class Default implements OptionChoice {
        private String name;
        private String value;
    }
}
