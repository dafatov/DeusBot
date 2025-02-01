package ru.demetrious.deus.bot.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Data
@Accessors(chain = true)
public class CommandData {
    private Name name;
    private String description;
    private List<OptionData> options = new ArrayList<>();

    @AllArgsConstructor
    @RequiredArgsConstructor
    public enum Name {
        AI_IMAGE("ai", "image"),
        CLEAR("clear"),
        EVENT_CREATE("event", "create"),
        EVENT_HELP("event", "help"),
        EVENT_REMOVE("event", "remove"),
        EVENT_SHOW("event", "show"),
        FIRST("first"),
        LOOP("loop"),
        MOVE("move"),
        PAUSE("pause"),
        PING("ping"),
        PLAY("play"),
        PUBLICIST_REMOVE("publicist", "remove"),
        PUBLICIST_SET("publicist", "set"),
        PUBLICIST_SHOW("publicist", "show"),
        QUEUE("queue"),
        REMOVE("remove"),
        SHIKIMORI("shikimori"),
        SHUFFLE("shuffle"),
        SKIP("skip"),
        STATISTIC_COMMAND("statistic", "command"),
        STATISTIC_MESSAGE("statistic", "message"),
        STATISTIC_SESSION("statistic", "session"),
        STATISTIC_VOICE("statistic", "voice");

        @Getter
        private final String commandName;
        @Getter
        private String groupName;
        @Getter
        private String subcommandName;

        Name(String commandName, String subcommandName) {
            this.commandName = commandName;
            this.subcommandName = subcommandName;
        }

        public static Name from(String commandName, String groupName, String subcommandName) {
            return stream(values())
                .filter(value -> StringUtils.equals(value.commandName, commandName) && StringUtils.equals(value.groupName, groupName) &&
                    StringUtils.equals(value.subcommandName, subcommandName))
                .findFirst()
                .orElseThrow();
        }

        public String stringify() {
            return Stream.of(commandName, groupName, subcommandName)
                .filter(StringUtils::isNotBlank)
                .collect(joining(" "));
        }
    }
}
