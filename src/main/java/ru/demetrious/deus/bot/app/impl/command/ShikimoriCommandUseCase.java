package ru.demetrious.deus.bot.app.impl.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.anime.GetAnimeOutbound;
import ru.demetrious.deus.bot.app.api.anime.ImportAnimeOutbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.ShikimoriCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.ImportAnimeContext;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.lang.Integer.parseInt;
import static java.time.Instant.now;
import static java.util.Optional.empty;
import static ru.demetrious.deus.bot.domain.CommandData.Name.SHIKIMORI;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.ANILIST_REGISTRATION_ID;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.SHIKIMORI_REGISTRATION_ID;

@RequiredArgsConstructor
@Slf4j
@Component
public class ShikimoriCommandUseCase extends PlayerCommand implements ShikimoriCommandInbound {
    private static final String STRING_OPTION_METHOD = "method";
    private static final String METHOD_CHOICE_FILE = "file";
    private static final String METHOD_CHOICE_ANILIST = "anilist";

    private final GetAnimeOutbound getAnimeOutbound;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final ImportAnimeOutbound importAnimeOutbound;
    private final GetStringOptionOutbound getStringOptionOutbound;

    @Value("${ANILIST_URL}")
    private String anilistUrl;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(SHIKIMORI)
            .setDescription("Экспорт списка с shikimori")
            .setOptions(List.of(new OptionData()
                .setType(STRING)
                .setName(STRING_OPTION_METHOD)
                .setDescription("Метод экспорта")
                .setRequired(true)
                .setChoices(List.of(
                    new OptionChoice()
                        .setName("Получить файлом")
                        .setValue(METHOD_CHOICE_FILE),
                    new OptionChoice()
                        .setName("Импортировать на anilist.co")
                        .setValue(METHOD_CHOICE_ANILIST)
                ))));
    }

    @Override
    public boolean isDefer() {
        String userId = getUserIdOutbound.getUserId();

        return getStringOptionOutbound.getStringOption(STRING_OPTION_METHOD)
            .map(method -> authorizationComponent.authorize(SHIKIMORI_REGISTRATION_ID, userId).isPresent() && (METHOD_CHOICE_FILE.equals(method)
                || METHOD_CHOICE_ANILIST.equals(method) && authorizationComponent.authorize(ANILIST_REGISTRATION_ID, userId).isPresent()))
            .orElseThrow();
    }

    @Override
    public void execute() {
        String userId = getUserIdOutbound.getUserId();
        Optional<OAuth2AuthorizedClient> shikimoriUser = authorizationComponent.authorize(SHIKIMORI_REGISTRATION_ID, userId);

        if (shikimoriUser.isEmpty()) {
            notifyOutbound.notifyUnauthorized(authorizationComponent.getData(userId, SHIKIMORI_REGISTRATION_ID));
            return;
        }

        String method = getStringOptionOutbound.getStringOption(STRING_OPTION_METHOD).orElseThrow();
        Optional<OAuth2AuthorizedClient> anilistUser = empty();

        if (METHOD_CHOICE_ANILIST.equals(method)) {
            anilistUser = authorizationComponent.authorize(ANILIST_REGISTRATION_ID, userId);

            if (anilistUser.isEmpty()) {
                notifyOutbound.notifyUnauthorized(authorizationComponent.getData(userId, ANILIST_REGISTRATION_ID));
                return;
            }
        }

        MessageData messageData = switch (method) {
            case METHOD_CHOICE_FILE -> new MessageData()
                .setFiles(List.of(new MessageFile()
                    .setData(getAnimeListXml())
                    .setName("%s_animes.xml".formatted(shikimoriUser.get().getPrincipalName()))));
            case METHOD_CHOICE_ANILIST -> {
                String anilistPrincipalName = anilistUser.get().getPrincipalName();
                ImportAnimeContext importAnimeContext = importAnimeOutbound.execute(getAnimeOutbound.getAnimeList(), parseInt(anilistPrincipalName));

                yield new MessageData().setEmbeds(List.of(new MessageEmbed()
                    .setColor(MessageEmbed.ColorEnum.INFO)
                    .setTitle("Список shikimori успешно импортирован на anilist.co")
                    .setUrl("%s/user/%s/animelist".formatted(anilistUrl, anilistPrincipalName))
                    .setDescription("""
                        Обновлено или добавлено: %d
                        Удалено: %d
                        """.formatted(importAnimeContext.getChangesCount(), importAnimeContext.getRemovedCount()))
                    .setTimestamp(now())));
            }
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };

        notifyOutbound.notify(messageData);
        log.info("Обработка списка успешно выполнена по методу {}", method);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private byte[] getAnimeListXml() {
        try {
            return getAnimeOutbound.getAnimeListXml();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
