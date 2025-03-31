package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.UUID;
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
import static org.apache.commons.lang3.StringUtils.isBlank;
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
    private static final String GAME_OPTION = "game";

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
                    .setName(GAME_OPTION)
                    .setDescription("Игра")
                    .setRequired(true)
                    .setAutoComplete(true),
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
            switch (focusedOption.getName()) {
                case ANIME_OPTION -> optionList = getFranchiseOutbound.getFranchiseList().stream()
                    .flatMap(f -> f.getTitles().stream()
                        .filter(title -> containsIgnoreCase(title, focusedOption.getValue()))
                        .map(t -> new OptionChoice()
                            .setValue(f.getName())
                            .setName(t)))
                    .limit(MAX_CHOICES)
                    .toList();
                case GAME_OPTION -> optionList = aniguessrGamesHolder.getGames().stream()
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

            b(notifyOutbound).notify(messageData);
            log.warn("Произошла попытка играть в игру вне созданного трэда");
            return;
        }

        String franchiseName = getStringOptionOutbound.getStringOption(ANIME_OPTION)
            .orElseThrow();
        Franchise franchise = getFranchiseOutbound.getFranchiseList().stream()
            .filter(f -> f.getName().equals(franchiseName))
            .findFirst()
            .orElseThrow();
        Status status = aniguessrGamesHolder.guess(gameId, franchise);

        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle(switch (status) {
                case GUESSED -> "Угадал. Молодец (за %s попыток)".formatted(aniguessrGamesHolder.getGuessesCount(gameId));
                case DUPLICATE -> "Уже было попробуй заново";
                case ADDED -> "Попытка засчитана (#%s)".formatted(aniguessrGamesHolder.getGuessesCount(gameId));
            })
            .setDescription(status == DUPLICATE ? "Baka~" : aniguessrGamesHolder.getLastGuess(gameId))
            .setFooter(gameId.toString())));
        b(notifyOutbound).notify(messageData);

        if (status == GUESSED) {
            aniguessrGamesHolder.remove(gameId);
            leaveThreadOutbound.leaveThread(threadId);
        }
        log.info("Попытка угадать успешно засчитана");
    }
}
