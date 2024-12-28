package ru.demetrious.deus.bot.app.impl.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.anime.GetAnimeOutbound;
import ru.demetrious.deus.bot.app.api.anime.ImportAnimeOutbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.ShikimoriCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.user.FindLinkUserOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.ImportAnimeContext;
import ru.demetrious.deus.bot.domain.LinkUser;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.OptionChoice;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.fw.config.security.LinkAuthorizationComponent;

import static java.time.Instant.now;
import static ru.demetrious.deus.bot.domain.CommandData.Name.SHIKIMORI;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;

@RequiredArgsConstructor
@Slf4j
@Component
public class ShikimoriCommandUseCase extends PlayerCommand implements ShikimoriCommandInbound {
    private static final String SHIKIMORI_REGISTRATION_ID = "shikimori";
    private static final String ANILIST_REGISTRATION_ID = "anilist";

    private final GetAnimeOutbound getAnimeOutbound;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;
    private final FindLinkUserOutbound findLinkUserOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final LinkAuthorizationComponent linkAuthorizationComponent;
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
                .setName("method")
                .setDescription("Метод экспорта")
                .setRequired(true)
                .setChoices(List.of(
                    new OptionChoice()
                        .setName("file")
                        .setValue("file"),
                    new OptionChoice()
                        .setName("anilist")
                        .setValue("anilist")
                ))));
    }

    @Override
    public void execute() {
        String userId = getUserIdOutbound.getUserId();
        Optional<LinkUser> shikimoriUser = findLinkUserOutbound.findById(new LinkUser.LinkUserKey()
            .setLinkedRegistrationId(SHIKIMORI_REGISTRATION_ID)
            .setDiscordPrincipalName(userId));

        if (shikimoriUser.isEmpty()) {
            notifyOutbound.notifyUnauthorized(linkAuthorizationComponent.getUrl(userId, SHIKIMORI_REGISTRATION_ID));
            return;
        }

        String method = getStringOptionOutbound.getStringOption("method").orElseThrow();
        Map<String, Object> animeList = getAnimeOutbound.getAnimeList();

        if ("file".equals(method)) {
            byte[] bytes = getBytes(animeList);
            MessageData messageData = new MessageData()
                .setFiles(List.of(new MessageFile()
                    .setData(bytes)
                    .setName("%s_animes.xml".formatted(shikimoriUser.get().getLinkedPrincipalName()))));

            notifyOutbound.notify(messageData);
            return;
        }

        if ("anilist".equals(method)) {
            Optional<LinkUser> anilistUser = findLinkUserOutbound.findById(new LinkUser.LinkUserKey()
                .setLinkedRegistrationId(ANILIST_REGISTRATION_ID)
                .setDiscordPrincipalName(userId));

            if (anilistUser.isEmpty()) {
                notifyOutbound.notifyUnauthorized(linkAuthorizationComponent.getUrl(userId, ANILIST_REGISTRATION_ID));
                return;
            }

            //noinspection unchecked
            ImportAnimeContext importAnimeContext = importAnimeOutbound.execute((List<Map<String, String>>) animeList.get("anime"));
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(MessageEmbed.ColorEnum.INFO)
                .setTitle("Список shikimori успешно импортирован на anilist.co")
                .setUrl("%s/user/%s/animelist".formatted(anilistUrl, importAnimeContext.getUserId()))
                .setDescription("""
                    Обновлено или добавлено: %d
                    Удалено: %d
                    """.formatted(importAnimeContext.getChangesCount(), importAnimeContext.getRemovedCount()))
                .setTimestamp(now())));
            notifyOutbound.notify(messageData);
        }
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private byte[] getBytes(Map<String, ?> animeList) {
        try {
            //noinspection unchecked
            ((List<Map<String, Object>>) animeList.get("anime"))
                .forEach(stringStringLinkedHashMap -> stringStringLinkedHashMap.putAll(Map.of(
                    "my_start_date", List.of("0000-00-00"),
                    "my_finish_date", List.of("0000-00-00")
                )));

            return new XmlMapper()
                .writerWithDefaultPrettyPrinter()
                .withRootName("myanimelist")
                .writeValueAsBytes(animeList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
