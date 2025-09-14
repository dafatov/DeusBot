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

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public enum Name {
        AI_DEUS("ai", "deus"),
        AI_IMAGE("ai", "image"),
        ANIGUESSR_CONCEDE("aniguessr", "concede"),
        ANIGUESSR_GUESS("aniguessr", "guess"),
        ANIGUESSR_START("aniguessr", "start"),
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
        REVERSE1999_CHARACTERS_SET("reverse1999", "characters", "set"),
        REVERSE1999_CHARACTERS_SHOW("reverse1999", "characters", "show"),
        REVERSE1999_PULLS_IMPORT("reverse1999", "pulls", "import"),
        REVERSE1999_PULLS_SHOW("reverse1999", "pulls", "show"),
        SHIKIMORI("shikimori"),
        SHUFFLE("shuffle"),
        SKIP("skip"),
        STATISTIC_COMMAND("statistic", "command"),
        STATISTIC_MESSAGE("statistic", "message"),
        STATISTIC_SESSION("statistic", "session"),
        STATISTIC_VOICE("statistic", "voice");

        private final String commandName;
        private String groupName;
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
