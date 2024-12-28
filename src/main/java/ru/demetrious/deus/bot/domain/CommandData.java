package ru.demetrious.deus.bot.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.MapUtils.getObject;

@Data
@Accessors(chain = true)
public class CommandData {
    private Name name;
    private String description;
    private List<OptionData> options = new ArrayList<>();

    @RequiredArgsConstructor
    public enum Name {
        CLEAR("clear"),
        FIRST("first"),
        LOOP("loop"),
        MOVE("move"),
        PAUSE("pause"),
        PING("ping"),
        PLAY("play"),
        QUEUE("queue"),
        REMOVE("remove"),
        SHIKIMORI("shikimori"),
        SHUFFLE("shuffle"),
        SKIP("skip");

        private final static Map<String, Name> VALUES_MAP = new HashMap<>(stream(values()).collect(toMap(Name::getValue, identity())));

        @Getter
        private final String value;

        public static Name fromValue(String value) {
            return getObject(VALUES_MAP, value, null);
        }
    }
}
