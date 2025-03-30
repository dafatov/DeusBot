package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.anime.GetFranchiseOutbound;
import ru.demetrious.deus.bot.app.api.command.AniguessrStartCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.Franchise;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static ru.demetrious.deus.bot.domain.CommandData.Name.ANIGUESSR_START;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.SHIKIMORI_REGISTRATION_ID;

@RequiredArgsConstructor
@Slf4j
@Component
public class AniguessrStartCommandUseCase implements AniguessrStartCommandInbound {
    private final GetFranchiseOutbound getFranchiseOutbound;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;
    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
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
        UUID id = aniguessrGamesHolder.create(franchise);
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setTitle("Игра начата")
            .setDescription("Игра с идентификатором ```%s```".formatted(id))));

        notifyOutbound.notify(new MessageData().setContent("## Предлагается вести игру в отдельном трэде"));
        notifyOutbound.notify(messageData, "Игра %s".formatted(id));
        log.info("Игра успешно начата: {}", franchise.getName());
    }
}
