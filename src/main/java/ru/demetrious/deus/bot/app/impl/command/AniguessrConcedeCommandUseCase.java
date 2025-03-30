package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.command.AniguessrConcedeCommandInbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.option.GetFocusedOptionOutbound;
import ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder;
import ru.demetrious.deus.bot.domain.AutocompleteOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.Franchise;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static ru.demetrious.deus.bot.domain.CommandData.Name.ANIGUESSR_CONCEDE;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;

@RequiredArgsConstructor
@Slf4j
@Component
public class AniguessrConcedeCommandUseCase implements AniguessrConcedeCommandInbound {
    private static final String GAME_OPTION = "game";

    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final AniguessrGamesHolder aniguessrGamesHolder;
    private final ReplyChoicesOutbound replyChoicesOutbound;
    private final GetFocusedOptionOutbound getFocusedOptionOutbound;
    private final GetStringOptionOutbound getStringOptionOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(ANIGUESSR_CONCEDE)
            .setDescription("Признает поражение и завершает игру")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(GAME_OPTION)
                    .setDescription("Игра")
                    .setRequired(true)
                    .setAutoComplete(true)
            ));
    }

    @Override
    public void onAutocomplete() {
        AutocompleteOption focusedOption = getFocusedOptionOutbound.getFocusedOption();
        List<OptionChoice> optionList = List.of();

        if (!aniguessrGamesHolder.getGames().isEmpty()) {
            if (focusedOption.getName().equals(GAME_OPTION)) {
                optionList = aniguessrGamesHolder.getGames().stream()
                    .filter(title -> containsIgnoreCase(title.toString(), focusedOption.getValue()))
                    .map(g -> new OptionChoice()
                        .setName(g.toString())
                        .setValue(g.toString()))
                    .toList();
            }
        }

        replyChoicesOutbound.replyChoices(optionList);
    }

    @Override
    public void execute() {
        UUID id = getStringOptionOutbound.getStringOption(GAME_OPTION)
            .map(UUID::fromString)
            .orElseThrow();
        Franchise franchise = aniguessrGamesHolder.concede(id);
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Игра сдана")
            .setDescription("Загадана была франшиза: %s".formatted(franchise.getName()))));

        notifyOutbound.notify(messageData);
        log.info("Игра успешно сдана");
    }
}
