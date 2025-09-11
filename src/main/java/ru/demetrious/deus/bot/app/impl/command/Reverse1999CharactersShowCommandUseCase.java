package ru.demetrious.deus.bot.app.impl.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseCharacterListOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999ShowCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.pull.FindPullsDataOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.app.impl.canvas.ReverseCharactersCanvas;
import ru.demetrious.deus.bot.app.impl.canvas.ReverseCharactersCanvas.CharacterDrawable;
import ru.demetrious.deus.bot.domain.Character;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.Pull;
import ru.demetrious.deus.bot.domain.PullsData;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.lang.Math.min;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.demetrious.deus.bot.domain.Character.CHARACTERS_MAX_PORTRAIT;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_CHARACTERS_SET;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_CHARACTERS_SHOW;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_IMPORT;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.GOOGLE_REGISTRATION_ID;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999CharactersShowCommandUseCase implements Reverse1999ShowCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseCharacterListOutbound getReverseCharacterListOutbound;
    private final FindPullsDataOutbound findPullsDataOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_CHARACTERS_SHOW)
            .setDescription("Отображает список персонажей пользователя в виде картинки");
    }

    @Override
    public void execute() {
        String userId = getUserIdOutbound.getUserId();
        Optional<OAuth2AuthorizedClient> googleUser = authorizationComponent.authorize(GOOGLE_REGISTRATION_ID, userId);

        if (googleUser.isEmpty()) {
            notifyOutbound.notifyUnauthorized(authorizationComponent.getData(userId, GOOGLE_REGISTRATION_ID));
            return;
        }

        MessageData messageData = findPullsDataOutbound.findPullsData()
            .map(pullsData -> getCharacterDrawableList(pullsData, getReverseCharacterListOutbound.getReverseCharacterList()))
            .filter(characterList -> !characterList.isEmpty())
            .map(ReverseCharactersCanvas::new)
            .map(ReverseCharactersCanvas::createFile)
            .map(List::of)
            .map(new MessageData()::setFiles)
            .orElseGet(() -> new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setTitle("Списка круток нет")
                .setDescription("Используй команду `/%s` чтобы импортировать из игры или команду `/%s` для наполнения вручную"
                    .formatted(REVERSE1999_IMPORT.stringify(), REVERSE1999_CHARACTERS_SET.stringify())))));
        notifyOutbound.notify(messageData);
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private @NotNull List<CharacterDrawable> getCharacterDrawableList(@NotNull PullsData pullsData, @NotNull Map<Integer, Character> characterMap) {
        Map<Integer, Integer> pulledCharacterMap = pullsData.getPullList().stream()
            .map(Pull::getSummonIdList)
            .flatMap(Collection::stream)
            .collect(groupingBy(
                identity(),
                collectingAndThen(counting(), c -> min(c.intValue(), CHARACTERS_MAX_PORTRAIT))
            ));

        pullsData.getCharacterCorrelationMap().forEach((key, count) -> pulledCharacterMap.merge(key, count, Integer::sum));
        return pulledCharacterMap
            .entrySet().stream()
            .map(entry -> new CharacterDrawable(characterMap.get(entry.getKey()), entry.getValue()))
            .collect(toList());
    }
}
