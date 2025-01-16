package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.duplex.jda.mapper.CommandDataMapper;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.interaction.AutocompleteInteractionInbound;
import ru.demetrious.deus.bot.app.api.option.GetFocusedOptionOutbound;
import ru.demetrious.deus.bot.domain.AutocompleteOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.OptionChoice;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static ru.demetrious.deus.bot.fw.config.spring.SpringConfig.SCOPE_THREAD;

@RequiredArgsConstructor
@Scope(value = SCOPE_THREAD, proxyMode = TARGET_CLASS)
@Component
public class AutocompleteAdapter extends BaseAdapter<CommandAutoCompleteInteractionEvent, AutocompleteInteractionInbound> implements ReplyChoicesOutbound,
    GetFocusedOptionOutbound {
    private final CommandDataMapper commandDataMapper;

    @Override
    public CommandData.Name getCommandName() {
        return CommandData.Name.from(getEvent().getName(), getEvent().getSubcommandGroup(), getEvent().getSubcommandName());
    }

    @Override
    public String getUserId() {
        return getEvent().getUser().getId();
    }

    @Override
    public void replyChoices(List<OptionChoice> choiceList) {
        getEvent().replyChoices(commandDataMapper.mapChoice(choiceList)).queue();
    }

    @Override
    public AutocompleteOption getFocusedOption() {
        return commandDataMapper.mapAutocompleteOption(getEvent().getFocusedOption());
    }
}
