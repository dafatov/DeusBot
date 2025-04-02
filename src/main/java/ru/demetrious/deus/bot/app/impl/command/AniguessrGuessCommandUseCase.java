package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.anime.GetFranchiseOutbound;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.channel.GetChannelIdOutbound;
import ru.demetrious.deus.bot.app.api.command.AniguessrGuessCommandInbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.option.GetFocusedOptionOutbound;
import ru.demetrious.deus.bot.app.api.thread.LeaveThreadOutbound;
import ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder;
import ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder.Status;
import ru.demetrious.deus.bot.domain.AutocompleteOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.Franchise;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound.MAX_CHOICES;
import static ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder.Status.DUPLICATE;
import static ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder.Status.GUESSED;
import static ru.demetrious.deus.bot.domain.CommandData.Name.ANIGUESSR_GUESS;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@RequiredArgsConstructor
@Slf4j
@Component
public class AniguessrGuessCommandUseCase implements AniguessrGuessCommandInbound {
    private static final String ANIME_OPTION = "anime";

    private final GetFranchiseOutbound getFranchiseOutbound;
    private final AniguessrGamesHolder aniguessrGamesHolder;
    private final ReplyChoicesOutbound replyChoicesOutbound;
    private final GetFocusedOptionOutbound getFocusedOptionOutbound;
    private final GetStringOptionOutbound getStringOptionOutbound;
    private final List<NotifyOutbound<?>> notifyOutbound;
    private final GetChannelIdOutbound<SlashCommandInteractionInbound> getChannelIdOutbound;
    private final LeaveThreadOutbound<SlashCommandInteractionInbound> leaveThreadOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(ANIGUESSR_GUESS)
            .setDescription("Позволяет попробовать угодать")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(ANIME_OPTION)
                    .setDescription("Аниме")
                    .setRequired(true)
                    .setAutoComplete(true)
            ));
    }

    @Override
    public void onAutocomplete() {
        AutocompleteOption focusedOption = getFocusedOptionOutbound.getFocusedOption();
        List<OptionChoice> optionList = List.of();

        if (!aniguessrGamesHolder.getGames().isEmpty()) {
            optionList = getFranchiseOutbound.getFranchiseList().stream()
                .flatMap(f -> f.getTitles().stream()
                    .filter(title -> containsIgnoreCase(title, focusedOption.getValue()))
                    .map(t -> new OptionChoice()
                        .setValue(f.getName())
                        .setName(t)))
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

            b(notifyOutbound).notify(messageData);
            log.warn("Произошла попытка играть в игру вне созданного трэда");
            return;
        }

        String threadId = channelIdOptional.get();
        String franchiseName = getStringOptionOutbound.getStringOption(ANIME_OPTION)
            .orElseThrow();
        Franchise franchise = getFranchiseOutbound.getFranchiseList().stream()
            .filter(f -> f.getName().equals(franchiseName))
            .findFirst()
            .orElseThrow();
        Status status = aniguessrGamesHolder.guess(threadId, franchise);

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle(switch (status) {
                case GUESSED -> "Угадал. Молодец (за %s попыток)".formatted(aniguessrGamesHolder.getGuessesCount(threadId));
                case DUPLICATE -> "Уже было попробуй заново";
                case ADDED -> "Попытка засчитана (#%s)".formatted(aniguessrGamesHolder.getGuessesCount(threadId));
            })
            .setDescription(status == DUPLICATE ? "Baka~" : aniguessrGamesHolder.getLastGuess(threadId))));
        b(notifyOutbound).notify(messageData);

        if (status == GUESSED) {
            aniguessrGamesHolder.remove(threadId);
            leaveThreadOutbound.leaveThread(threadId);
        }
        log.info("Попытка угадать успешно засчитана");
    }
}
