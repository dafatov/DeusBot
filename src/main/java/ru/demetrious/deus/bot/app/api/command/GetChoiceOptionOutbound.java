package ru.demetrious.deus.bot.app.api.command;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import ru.demetrious.deus.bot.domain.OptionChoice;

@FunctionalInterface
public interface GetChoiceOptionOutbound {
    @NullMarked
    <T extends Enum<T> & OptionChoice> Optional<T> getChoiceOption(String name, Class<T> type);
}
