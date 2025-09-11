package ru.demetrious.deus.bot.app.impl.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseCharacterListOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999ShowCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.pull.FindPullsDataOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.app.impl.canvas.ReversePullTypeCanvas;
import ru.demetrious.deus.bot.domain.Character;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.PullsData;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.util.stream.Collectors.groupingBy;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_IMPORT;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_PULLS_SHOW;
import static ru.demetrious.deus.bot.domain.Pull.COLLABORATION_POOL_TYPE;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.GOOGLE_REGISTRATION_ID;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999PullsShowCommandUseCase implements Reverse1999ShowCommandInbound {
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseCharacterListOutbound getReverseCharacterListOutbound;
    private final FindPullsDataOutbound findPullsDataOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;

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

        Map<Integer, Character> characterMap = getReverseCharacterListOutbound.getReverseCharacterList();
        List<MessageFile> messageFileList = findPullsDataOutbound.findPullsData()
            .map(PullsData::getPullList).stream()
            .flatMap(Collection::stream)
            .collect(groupingBy(pull -> Objects.equals(pull.getPoolType(), COLLABORATION_POOL_TYPE) ? pull.getPoolId() : pull.getPoolType()))
            .entrySet().stream()
            .map(entry -> new ReversePullTypeCanvas(entry.getValue(), characterMap, entry.getKey()))
            .map(ReversePullTypeCanvas::createFile)
            .toList();
        MessageData messageData;

        if (messageFileList.isEmpty()) {
            messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setTitle("Списка круток нет")
                .setDescription("Используй команду `/%s` чтобы импортировать из игры".formatted(REVERSE1999_IMPORT.stringify()))));
        } else {
            messageData = new MessageData().setFiles(messageFileList);
        }
        notifyOutbound.notify(messageData);
    }
}
