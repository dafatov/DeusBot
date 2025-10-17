package ru.demetrious.deus.bot.app.impl.command;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseCharacterListOutbound;
import ru.demetrious.deus.bot.app.api.command.GetAttachmentOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999CharactersImportCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.pull.FindPullsDataOutbound;
import ru.demetrious.deus.bot.app.api.pull.UpdatePullsDataOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.domain.CharactersExport;
import ru.demetrious.deus.bot.domain.CharactersExport.CharacterExport;
import ru.demetrious.deus.bot.domain.AttachmentOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.domain.Pull;
import ru.demetrious.deus.bot.domain.PullsData;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.lang.Math.min;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.function.Failable.asFunction;
import static ru.demetrious.deus.bot.domain.Character.MAX_PORTRAIT;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_CHARACTERS_IMPORT;
import static ru.demetrious.deus.bot.domain.OptionData.Type.ATTACHMENT;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.GOOGLE_REGISTRATION_ID;
import static ru.demetrious.deus.bot.utils.DefaultUtils.defaultIfZero;
import static ru.demetrious.deus.bot.utils.JacksonUtils.getMapper;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999CharactersImportCommandUseCase implements Reverse1999CharactersImportCommandInbound {
    private static final String FILE_OPTION = "file";

    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseCharacterListOutbound getReverseCharacterListOutbound;
    private final FindPullsDataOutbound findPullsDataOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;
    private final GetAttachmentOptionOutbound getAttachmentOptionOutbound;
    private final UpdatePullsDataOutbound updatePullsDataOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_CHARACTERS_IMPORT)
            .setDescription("Позволяет импортировать реальное кол-во персонажей с учетом круток, изменяя коректировки")
            .setOptions(List.of(
                new OptionData()
                    .setType(ATTACHMENT)
                    .setName(FILE_OPTION)
                    .setDescription("Файл в формате JSON с новыми значениями; может быть неполным")
                    .setRequired(true)
            ));
    }

    @Override
    public void execute() {
        String userId = getUserIdOutbound.getUserId();
        Optional<OAuth2AuthorizedClient> googleUser = authorizationComponent.authorize(GOOGLE_REGISTRATION_ID, userId);

        if (googleUser.isEmpty()) {
            notifyOutbound.notifyUnauthorized(authorizationComponent.getData(userId, GOOGLE_REGISTRATION_ID));
            return;
        }

        Map<Integer, Integer> characters = getAttachmentOptionOutbound.getAttachmentOption(FILE_OPTION)
            .map(AttachmentOption::getUrl)
            .map(URI::create)
            .map(asFunction(URI::toURL))
            .map(asFunction(url -> getMapper().readValue(url, CharactersExport.class)))
            .map(CharactersExport::characters).stream()
            .flatMap(Collection::stream)
            .collect(toMap(CharacterExport::id, CharacterExport::count));
        PullsData pullsData = findPullsDataOutbound.findPullsData().orElseGet(PullsData::new);
        Map<Integer, Integer> characterPullsMap = pullsData.getPullList().stream()
            .map(Pull::getSummonIdList)
            .flatMap(List::stream)
            .collect(groupingBy(identity(), collectingAndThen(counting(), Math::toIntExact)));

        getReverseCharacterListOutbound.getReverseCharacterList().keySet().forEach(id -> pullsData.getCharacterCorrelationMap()
            .compute(id, (key, value) -> defaultIfZero(getPortraitCount(characters, id) - getPortraitCount(characterPullsMap, id))));
        updatePullsDataOutbound.updatePullsData(pullsData);
        log.debug("characterCorrelationMap={}", pullsData.getCharacterCorrelationMap());
        notifyOutbound.notify(new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Количество успешно импортировано. Данные коррекции обновлены"))));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private static Integer getPortraitCount(Map<Integer, Integer> characters, Integer id) {
        return min(characters.getOrDefault(id, 0), MAX_PORTRAIT);
    }
}
