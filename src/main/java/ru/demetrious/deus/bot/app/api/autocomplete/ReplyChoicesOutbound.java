package ru.demetrious.deus.bot.app.api.autocomplete;

import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.demetrious.deus.bot.domain.OptionChoice;

@FunctionalInterface
public interface ReplyChoicesOutbound {
    int MAX_CHOICES = OptionData.MAX_CHOICES;

    void replyChoices(List<OptionChoice> choiceList);
}
