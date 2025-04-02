package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.channel.GetChannelIdOutbound;
import ru.demetrious.deus.bot.app.api.command.AniguessrConcedeCommandInbound;
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

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound.MAX_CHOICES;
import static ru.demetrious.deus.bot.domain.CommandData.Name.ANIGUESSR_CONCEDE;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;

@RequiredArgsConstructor
@Slf4j
@Component
public class AniguessrConcedeCommandUseCase implements AniguessrConcedeCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final AniguessrGamesHolder aniguessrGamesHolder;
    private final ReplyChoicesOutbound replyChoicesOutbound;
    private final GetFocusedOptionOutbound getFocusedOptionOutbound;
    private final GetChannelIdOutbound<SlashCommandInteractionInbound> getChannelIdOutbound;
    private final LeaveThreadOutbound<SlashCommandInteractionInbound> leaveThreadOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(ANIGUESSR_CONCEDE)
            .setDescription("Признает поражение и завершает игру");
    }

    @Override
    public void onAutocomplete() {
        AutocompleteOption focusedOption = getFocusedOptionOutbound.getFocusedOption();
        List<OptionChoice> optionList = List.of();

        if (!aniguessrGamesHolder.getGames().isEmpty()) {
            optionList = aniguessrGamesHolder.getGames().stream()
                .filter(title -> containsIgnoreCase(title, focusedOption.getValue()))
                .map(g -> new OptionChoice()
                    .setName(g)
                    .setValue(g))
                .limit(MAX_CHOICES)
                .toList();
        }

        replyChoicesOutbound.replyChoices(optionList);
    }

    @Override
    public void execute() {
        Optional<String> channelIdOptional = getChannelIdOutbound.getChannelId();

        if (!channelIdOptional.map(aniguessrGamesHolder::exists).orElse(false)) {
            MessageData messageData = new MessageData()
                .setEmbeds(List.of(new MessageEmbed()
                    .setColor(WARNING)
                    .setTitle("Нельзя спамить!")
                    .setDescription("Нельзя играть не в созданном трэде, так как так мы засрем все что можно...")));

            notifyOutbound.notify(messageData);
            log.warn("Произошла попытка играть в игру вне созданного трэда");
            return;
        }

        String threadId = channelIdOptional.get();
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Игра сдана")
            .setDescription(aniguessrGamesHolder.remove(threadId))));
        notifyOutbound.notify(messageData);
        leaveThreadOutbound.leaveThread(threadId);
        log.info("Игра успешно сдана");
    }
}
