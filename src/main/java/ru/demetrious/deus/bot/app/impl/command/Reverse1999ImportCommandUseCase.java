package ru.demetrious.deus.bot.app.impl.command;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseSummonedCharacterListOutbound;
import ru.demetrious.deus.bot.app.api.command.GetStringOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999ImportCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.api.pull.FindPullsDataOutbound;
import ru.demetrious.deus.bot.app.api.pull.UpdatePullsDataOutbound;
import ru.demetrious.deus.bot.app.api.user.GetUserIdOutbound;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.domain.Pull;
import ru.demetrious.deus.bot.domain.PullsData;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.time.Instant.MIN;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_IMPORT;
import static ru.demetrious.deus.bot.domain.OptionData.Type.STRING;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.GOOGLE_REGISTRATION_ID;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999ImportCommandUseCase implements Reverse1999ImportCommandInbound {
    private static final String URL_OPTION = "url";

    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseSummonedCharacterListOutbound getReverseSummonedCharacterListOutbound;
    private final GetStringOptionOutbound getStringOptionOutbound;
    private final FindPullsDataOutbound findPullsDataOutbound;
    private final UpdatePullsDataOutbound updatePullsDataOutbound;
    private final AuthorizationComponent authorizationComponent;
    private final GetUserIdOutbound<SlashCommandInteractionInbound> getUserIdOutbound;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_IMPORT)
            .setDescription("Импортирует(обновляет) историю круток в гаче")
            .setOptions(List.of(
                new OptionData()
                    .setType(STRING)
                    .setName(URL_OPTION)
                    .setRequired(true)
                    .setDescription("Ссылка на историю из игры reverse 1999")
            ));
    }

    @Override
    public void execute() {
        Optional<List<Pull>> importPullList = getStringOptionOutbound.getStringOption(URL_OPTION)
            .map(URI::create)
            .flatMap(getReverseSummonedCharacterListOutbound::getReverseSummonedCharacterList);

        if (importPullList.isEmpty()) {
            notifyOutbound.notify(new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setTitle("Ссылка устарела...")
                .setDescription("Придется перезайти в игру или в меню аутентификации и вытащить ссылку заново(("))));
            return;
        }

        String userId = getUserIdOutbound.getUserId();
        Optional<OAuth2AuthorizedClient> googleUser = authorizationComponent.authorize(GOOGLE_REGISTRATION_ID, userId);

        if (googleUser.isEmpty()) {
            notifyOutbound.notifyUnauthorized(authorizationComponent.getData(userId, GOOGLE_REGISTRATION_ID));
            return;
        }

        PullsData pullsData = findPullsDataOutbound.findPullsData().orElseGet(PullsData::new);
        Instant lastPull = pullsData.getPullList().stream().map(Pull::getTime).max(Instant::compareTo).orElse(MIN);
        List<Pull> newPullList = importPullList.get().stream()
            .filter(pull -> pull.getTime().isAfter(lastPull))
            .toList();

        log.debug("newPullList?={}", newPullList.size());
        pullsData.getPullList().addAll(newPullList);
        updatePullsDataOutbound.updatePullsData(pullsData);

        MessageEmbed messageEmbed;
        if (newPullList.isEmpty()) {
            messageEmbed = new MessageEmbed()
                .setTitle("Сохраненные крутки актуальны")
                .setDescription("Последняя крутка: <t:%d:R>".formatted(lastPull.getEpochSecond()));
        } else {
            messageEmbed = new MessageEmbed()
                .setTitle("Импортированы новые крутки")
                .setDescription("""
                    Количество: %s
                    Добавлены после: <t:%d:R>
                    Последняя крутка: <t:%d:R>
                    """.formatted(
                    newPullList.size(),
                    lastPull.getEpochSecond(),
                    newPullList.stream()
                        .map(Pull::getTime)
                        .max(Instant::compareTo)
                        .map(Instant::getEpochSecond)
                        .get()
                ));
        }
        notifyOutbound.notify(new MessageData().setEmbeds(List.of(messageEmbed)));
    }
}
