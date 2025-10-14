package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseCharacterListOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999CharactersExportCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.pull.FindPullsDataOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.domain.CharactersExport;
import ru.demetrious.deus.bot.domain.CharactersExport.CharacterExport;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.Pull;
import ru.demetrious.deus.bot.domain.PullsData;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.lang.Math.min;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static ru.demetrious.deus.bot.domain.Character.MAX_PORTRAIT;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_CHARACTERS_EXPORT;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.GOOGLE_REGISTRATION_ID;
import static ru.demetrious.deus.bot.utils.JacksonUtils.writeValueAsString;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999CharactersExportCommandUseCase implements Reverse1999CharactersExportCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseCharacterListOutbound getReverseCharacterListOutbound;
    private final FindPullsDataOutbound findPullsDataOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_CHARACTERS_EXPORT)
            .setDescription("Позволяет экспортировать реальное кол-во персонажей, включая крутки и коректировки");
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
        Map<Integer, Integer> characterPullsMap = pullsData.getPullList().stream()
            .map(Pull::getSummonIdList)
            .flatMap(List::stream)
            .collect(groupingBy(identity(), collectingAndThen(counting(), Math::toIntExact)));
        List<CharacterExport> characterExportList = getReverseCharacterListOutbound.getReverseCharacterList().values().stream()
            .map(character -> new CharacterExport(character.getName(), character.getId(), getCount(character.getId(), pullsData, characterPullsMap)))
            .sorted(comparing(CharacterExport::name))
            .toList();
        CharactersExport charactersExport = new CharactersExport(1, characterExportList);

        notifyOutbound.notify(new MessageData().setFiles(List.of(new MessageFile()
            .setName("reverse1999_characters_count.json")
            .setData(writeValueAsString(charactersExport).getBytes(UTF_8)))));
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private static int getCount(Integer id, PullsData pullsData, Map<Integer, Integer> characterPullsMap) {
        return min(characterPullsMap.getOrDefault(id, 0), MAX_PORTRAIT) + pullsData.getCharacterCorrelationMap().getOrDefault(id, 0);
    }
}
