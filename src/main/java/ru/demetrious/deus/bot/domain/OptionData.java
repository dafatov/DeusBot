package ru.demetrious.deus.bot.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jspecify.annotations.NonNull;

import static java.util.Arrays.stream;

@Data
@Accessors(chain = true)
public class OptionData {
    private String name;
    private String description;
    private Type type;
    private boolean isRequired = false;
    private boolean isAutoComplete = false;
    private Integer minValue;
    private Integer maxValue;
    private List<OptionChoice> choices = new ArrayList<>();

    public OptionData setChoices(@NonNull OptionChoice[] choices) {
        this.choices = stream(choices).toList();
        return this;
    }

    public enum Type {
        ATTACHMENT, CHANNEL, INTEGER, STRING, USER
    }
}
