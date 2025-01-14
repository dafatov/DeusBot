package ru.demetrious.deus.bot.app.api.autocomplete;

import java.util.List;
import ru.demetrious.deus.bot.domain.OptionChoice;

@FunctionalInterface
public interface ReplyChoicesOutbound {
    void replyChoices(List<OptionChoice> choiceList);
}
