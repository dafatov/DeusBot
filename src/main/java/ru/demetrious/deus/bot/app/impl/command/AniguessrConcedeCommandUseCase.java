package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.channel.GetChannelIdOutbound;
import ru.demetrious.deus.bot.app.api.command.AniguessrConcedeCommandInbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.option.GetFocusedOptionOutbound;
import ru.demetrious.deus.bot.app.api.thread.LeaveThreadOutbound;
import ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder;
import ru.demetrious.deus.bot.domain.AutocompleteOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound.MAX_CHOICES;
import static ru.demetrious.deus.bot.domain.CommandData.Name.ANIGUESSR_CONCEDE;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
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
    private final GetChannelIdOutbound<SlashCommandInteractionInbound> getChannelIdOutbound;
    private final LeaveThreadOutbound<SlashCommandInteractionInbound> leaveThreadOutbound;

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
                    .limit(MAX_CHOICES)
                    .toList();
            }
        }

        replyChoicesOutbound.replyChoices(optionList);
    }

    @Override
    public void execute() {
        UUID gameId = getStringOptionOutbound.getStringOption(GAME_OPTION)
            .map(UUID::fromString)
            .orElseThrow();
        String threadId = aniguessrGamesHolder.getThreadId(gameId);

        if (!isBlank(threadId) && !getChannelIdOutbound.getChannelId().map(threadId::equals).orElse(false)) {
            MessageData messageData = new MessageData()
                .setEmbeds(List.of(new MessageEmbed()
                    .setColor(WARNING)
                    .setTitle("Нельзя спамить!")
                    .setDescription("Нельзя играть не в созданном трэде, так как так мы засрем все что можно...\nПерейди в <#%s>".formatted(threadId))));

            notifyOutbound.notify(messageData);
            log.warn("Произошла попытка играть в игру вне созданного трэда");
            return;
        }

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Игра сдана")
            .setDescription(aniguessrGamesHolder.remove(gameId))));
        notifyOutbound.notify(messageData);
        leaveThreadOutbound.leaveThread(threadId);
        log.info("Игра успешно сдана");
    }
}
