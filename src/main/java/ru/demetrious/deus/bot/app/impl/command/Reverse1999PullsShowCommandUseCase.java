package ru.demetrious.deus.bot.app.impl.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseDataOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999PullsShowCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.pull.FindPullsDataOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.app.impl.canvas.ReversePullTypeCanvas;
import ru.demetrious.deus.bot.app.impl.canvas.ReversePullTypeCanvas.GroupKey;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterData;
import ru.demetrious.deus.bot.domain.reverse1999.PullsData;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.collections4.ListUtils.partition;
import static ru.demetrious.deus.bot.app.api.message.NotifyOutbound.MAX_ATTACHMENTS;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_PULLS_IMPORT;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_PULLS_SHOW;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.GOOGLE_REGISTRATION_ID;
import static ru.demetrious.deus.bot.utils.JacksonUtils.getMapper;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999PullsShowCommandUseCase implements Reverse1999PullsShowCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseDataOutbound getReverseDataOutbound;
    private final FindPullsDataOutbound findPullsDataOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;
    private final PoolNameComponent poolNameComponent;
    private final DropCharacterDataComponent dropCharacterDataComponent;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_PULLS_SHOW)
            .setDescription("Отображает список круток пользователя в виде картинок по типу баннера");
    }

    @Override
    public void execute() {
        String userId = getUserIdOutbound.getUserId();
        Optional<OAuth2AuthorizedClient> googleUser = authorizationComponent.authorize(GOOGLE_REGISTRATION_ID, userId);

        if (googleUser.isEmpty()) {
            notifyOutbound.notifyUnauthorized(authorizationComponent.getData(userId, GOOGLE_REGISTRATION_ID));
            return;
        }

        Map<Integer, CharacterData> characterMap = new HashMap<>() {{
            putAll(getReverseDataOutbound.getReverseData().getCharacters());
            putAll(dropCharacterDataComponent.getDrops());
        }};
        List<MessageFile> messageFileList = findPullsDataOutbound.findPullsData()
            .map(PullsData::getPullList).stream()
            .flatMap(Collection::stream)
            .collect(groupingBy(pull -> new GroupKey(pull.getPoolType(), poolNameComponent.hasOnlyType(pull.getPoolType()) ? null : pull.getPoolId())))
            .entrySet().stream()
            .map(entry -> new ReversePullTypeCanvas(entry.getValue(), characterMap, entry.getKey(), poolNameComponent::getName))
            .map(ReversePullTypeCanvas::createFile)
            .toList();

        if (messageFileList.isEmpty()) {
            notifyOutbound.notify(new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setTitle("Списка круток нет")
                .setDescription("Используй команду `/%s` чтобы импортировать из игры".formatted(REVERSE1999_PULLS_IMPORT.stringify())))));
        } else {
            partition(messageFileList, MAX_ATTACHMENTS).stream()
                .map(new MessageData()::setFiles)
                .forEach(notifyOutbound::notify);
        }
    }

    @Configuration
    public static class PoolNameComponent {
        private final Map<String, Object> pools;

        public PoolNameComponent(@Value("${REVERSE1999_POOLS:{}}") String poolsJson) throws JsonProcessingException {
            this.pools = getMapper().readValue(poolsJson, new TypeReference<>() {
            });
        }

        public boolean hasOnlyType(int typeId) {
            return pools.get(String.valueOf(typeId)) instanceof String;
        }

        public Optional<String> getName(@NotNull GroupKey groupKey) {
            return switch (pools.get(String.valueOf(groupKey.typeId()))) {
                case String name -> name.describeConstable();
                case Map<?, ?> poolTypeMap -> ofNullable(groupKey.id())
                    .map(String::valueOf)
                    .map(poolTypeMap::get)
                    .map(String::valueOf);
                case null, default -> empty();
            };
        }
    }

    @Configuration
    public static class DropCharacterDataComponent {
        @Getter
        private final Map<Integer, CharacterData> drops;

        public DropCharacterDataComponent(@Value("${REVERSE1999_DROPS:{}}") String dropsJson) throws JsonProcessingException {
            this.drops = getMapper().readValue(dropsJson, new TypeReference<>() {
            });
        }
    }
}
