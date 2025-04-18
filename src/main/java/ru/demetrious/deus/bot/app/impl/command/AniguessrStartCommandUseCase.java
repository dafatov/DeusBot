package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.anime.GetFranchiseOutbound;
import ru.demetrious.deus.bot.app.api.command.AniguessrStartCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.thread.CreateThreadOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.Franchise;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static ru.demetrious.deus.bot.domain.CommandData.Name.ANIGUESSR_START;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.SHIKIMORI_REGISTRATION_ID;

@RequiredArgsConstructor
@Slf4j
@Component
public class AniguessrStartCommandUseCase implements AniguessrStartCommandInbound {
    private final GetFranchiseOutbound getFranchiseOutbound;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final CreateThreadOutbound<SlashCommandInteractionInbound> createThreadOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final AniguessrGamesHolder aniguessrGamesHolder;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(ANIGUESSR_START)
            .setDescription("Запускает угадайку аниме");
    }

    @Override
    public void execute() {
        String userId = getUserIdOutbound.getUserId();
        Optional<OAuth2AuthorizedClient> shikimoriUser = authorizationComponent.authorize(SHIKIMORI_REGISTRATION_ID, userId);

        if (shikimoriUser.isEmpty()) {
            notifyOutbound.notifyUnauthorized(authorizationComponent.getData(userId, SHIKIMORI_REGISTRATION_ID));
            return;
        }

        List<Franchise> franchiseList = getFranchiseOutbound.getFranchiseList();
        Franchise franchise = franchiseList.get(new Random().nextInt(franchiseList.size()));
        MessageEmbed messageEmbed = createThreadOutbound.createThread("Aniguessr").map(
                threadId -> {
                    aniguessrGamesHolder.create(threadId, franchise);
                    return new MessageEmbed()
                        .setTitle("Игра создана")
                        .setDescription("""
                            Предлагается вести игру в отдельном трэде: <#%s>
                            Во франшизах не учитываются анонсированные тайтлы, тайтлы без оценки, без даты выхода, а также клипы, проморолики и рекламы
                            """.formatted(threadId));
                })
            .orElseGet(() -> new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Игра не создана")
                .setDescription("Что пошло нет так при создании отдельного трэда. Его можно создать только из текстового канала")
            );

        notifyOutbound.notify(new MessageData().setEmbeds(List.of(messageEmbed)));
        log.info("Игра успешно начата: {}", franchise.getName());
    }
}
