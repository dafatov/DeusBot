package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound;
import ru.demetrious.deus.bot.app.api.character.GetReverseCharacterListOutbound;
import ru.demetrious.deus.bot.app.api.command.GetIntegerOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999CharactersSetCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.option.GetFocusedOptionOutbound;
import ru.demetrious.deus.bot.app.api.pull.FindPullsDataOutbound;
import ru.demetrious.deus.bot.app.api.pull.UpdatePullsDataOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.domain.AutocompleteOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.domain.Pull;
import ru.demetrious.deus.bot.domain.PullsData;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.lang.Math.min;
import static java.lang.Math.toIntExact;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static ru.demetrious.deus.bot.app.api.autocomplete.ReplyChoicesOutbound.MAX_CHOICES;
import static ru.demetrious.deus.bot.domain.Character.MAX_PORTRAIT;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_CHARACTERS_SET;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.GOOGLE_REGISTRATION_ID;
import static ru.demetrious.deus.bot.utils.DefaultUtils.defaultIfZero;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999CharactersSetCommandUseCase implements Reverse1999CharactersSetCommandInbound {
    private static final String CHARACTER_OPTION = "character";
    private static final String COUNT_OPTION = "count";

    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseCharacterListOutbound getReverseCharacterListOutbound;
    private final GetStringOptionOutbound getStringOptionOutbound;
    private final FindPullsDataOutbound findPullsDataOutbound;
    private final UpdatePullsDataOutbound updatePullsDataOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;
    private final GetFocusedOptionOutbound getFocusedOptionOutbound;
    private final ReplyChoicesOutbound replyChoicesOutbound;
    private final GetIntegerOptionOutbound getIntegerOptionOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_CHARACTERS_SET)
            .setDescription("Позволяет ввести корректировки к автоматическому подсчету кол-ва персонажей")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(CHARACTER_OPTION)
                    .setDescription("Корректируемый персонаж")
                    .setRequired(true)
                    .setAutoComplete(true),
                new OptionData()
                    .setType(OptionData.Type.INTEGER)
                    .setName(COUNT_OPTION)
                    .setDescription("Реальное кол-во персонажей")
                    .setRequired(true)
                    .setMinValue(0)
                    .setMaxValue(MAX_PORTRAIT)
            ));
    }

    @Override
    public void onAutocomplete() {
        AutocompleteOption focusedOption = getFocusedOptionOutbound.getFocusedOption();

        if (!CHARACTER_OPTION.equals(focusedOption.getName())) {
            return;
        }

        List<OptionChoice> optionChoiceList = getReverseCharacterListOutbound.getReverseCharacterList().entrySet().stream()
            .filter(characterEntry -> containsIgnoreCase(characterEntry.getValue().getName(), focusedOption.getValue()))
            .map(characterEntry -> new OptionChoice()
                .setName(characterEntry.getValue().getName())
                .setValue(String.valueOf(characterEntry.getKey())))
            .limit(MAX_CHOICES)
            .toList();

        replyChoicesOutbound.replyChoices(optionChoiceList);
    }

    @Override
    public void execute() {
        String userId = getUserIdOutbound.getUserId();
        Optional<OAuth2AuthorizedClient> googleUser = authorizationComponent.authorize(GOOGLE_REGISTRATION_ID, userId);

        if (googleUser.isEmpty()) {
            notifyOutbound.notifyUnauthorized(authorizationComponent.getData(userId, GOOGLE_REGISTRATION_ID));
            return;
        }

        PullsData pullsData = findPullsDataOutbound.findPullsData().orElseGet(PullsData::new);
        Integer characterId = getStringOptionOutbound.getStringOption(CHARACTER_OPTION).map(Integer::valueOf).orElseThrow();
        int realCount = getIntegerOptionOutbound.getIntegerOption(COUNT_OPTION).orElseThrow();
        int count = min(MAX_PORTRAIT, toIntExact(pullsData.getPullList().stream()
            .map(Pull::getSummonIdList)
            .flatMap(List::stream)
            .filter(characterId::equals)
            .count()));
        int newCount = realCount - count;

        pullsData.getCharacterCorrelationMap().compute(characterId, (key, oldValue) -> defaultIfZero(newCount));
        log.debug("characterCorrelationMap={}", pullsData.getCharacterCorrelationMap());
        updatePullsDataOutbound.updatePullsData(pullsData);
        notifyOutbound.notify(new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Теперь для %s...".formatted(getReverseCharacterListOutbound.getReverseCharacterList().get(characterId).getName()))
            .setDescription("""
                ... установлено количество: %s
                -# что значит %s
                """.formatted(realCount, switch (realCount) {
                case 0 -> "**отсутствие персонажа**";
                case 1 -> "**наличие персонажа**";
                case 6 -> "**наличие персонажа** и **максимальная** конста";
                default -> "**наличие персонажа** и **%s конста**".formatted(realCount - 1);
            })))));
    }
}
