package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.command.CommandInbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.HelpCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.option.GetFocusedOptionOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.util.Comparator.comparing;
import static java.util.stream.Stream.concat;
import static ru.demetrious.deus.bot.domain.CommandData.Name;
import static ru.demetrious.deus.bot.domain.CommandData.Name.HELP;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;

@RequiredArgsConstructor
@Slf4j
@Component
public class HelpCommandUseCase implements HelpCommandInbound {
    private static final String COMMAND_OPTION = "command";

    private final ReplyChoicesOutbound replyChoicesOutbound;
    private final GetStringOptionOutbound getStringOptionOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetFocusedOptionOutbound getFocusedOptionOutbound;
    private final List<CommandInbound> commandInboundList;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(HELP)
            .setDescription("Помощь по командам бота")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(COMMAND_OPTION)
                    .setDescription("Наименование команды")
                    .setRequired(true)
                    .setAutoComplete(true)
            ));
    }

    @Override
    public String getHint() {
        return "Эта команда позволяет получить помощь при использовании бота DeuS";
    }

    @Override
    public void onAutocomplete() {
        List<OptionChoice> optionChoiceList = getAllCommandStream()
            .map(CommandInbound::getData)
            .map(CommandData::getName)
            .filter(commandName -> commandName.stringify().startsWith(getFocusedOptionOutbound.getFocusedOption().getValue()))
            .map(commandName -> new OptionChoice()
                .setName(commandName.stringify())
                .setValue(commandName.name()))
            .sorted(comparing(OptionChoice::getName))
            .toList();

        replyChoicesOutbound.replyChoices(optionChoiceList);
    }

    @Override
    public void execute() {
        Name commandName = getStringOptionOutbound.getStringOption(COMMAND_OPTION)
            .map(Name::valueOf)
            .orElseThrow();
        String hint = getAllCommandStream()
            .filter(command -> command.getData().getName() == commandName)
            .findFirst()
            .orElseThrow()
            .getHint();

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Помощь по команде /%s".formatted(commandName.stringify()))
            .setDescription(hint)));

        notifyOutbound.notify(messageData);
        log.info("Успешно выведена помощь по команде \"%s\"".formatted(commandName));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    @NotNull
    private Stream<CommandInbound> getAllCommandStream() {
        return concat(commandInboundList.stream(), Stream.of(this));
    }
}
