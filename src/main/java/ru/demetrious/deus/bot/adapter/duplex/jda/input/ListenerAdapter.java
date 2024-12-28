package ru.demetrious.deus.bot.adapter.duplex.jda.input;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.ButtonAdapter;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.GenericAdapter;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.ModalAdapter;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.SlashCommandAdapter;
import ru.demetrious.deus.bot.app.api.command.CommandInbound;
import ru.demetrious.deus.bot.domain.CommandData.Name;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.fw.config.security.LinkAuthorizationComponent;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.ERROR;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerAdapter extends net.dv8tion.jda.api.hooks.ListenerAdapter {
    private static final String CLIENT_REGISTRATION_ID = "discord";

    private final SlashCommandAdapter slashCommandAdapter;
    private final ModalAdapter modalAdapter;
    private final ButtonAdapter buttonAdapter;
    private final List<CommandInbound> commandList;
    private final LinkAuthorizationComponent linkAuthorizationComponent;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Value("${devs.ids:}")
    private List<String> devUserIdList;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        onInteraction(event, Name.fromValue(event.getName()), CommandInbound::execute, "запуском", slashCommandAdapter);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Name commandName = ofNullable(event.getMessage().getInteraction())
            .map(Message.Interaction::getName)
            .map(cN -> cN.split(" "))
            .map(split -> split[0])
            .map(Name::fromValue)
            .orElse(null);

        onInteraction(event, commandName, CommandInbound::onButton, "обработкой события кнопки", buttonAdapter);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        onInteraction(event, Name.fromValue(event.getModalId().split(" ")[0]), CommandInbound::onModal, "обработкой события модального окна", modalAdapter);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private <E extends GenericInteractionCreateEvent & IReplyCallback,
        A extends GenericAdapter<?, E, ?>> void onInteraction(@NotNull E event,
                                                              Name commandName,
                                                              Consumer<CommandInbound> executeConsumer,
                                                              String errorText,
                                                              A adapter) {
        adapter.setEvent(event);

        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(CLIENT_REGISTRATION_ID, event.getUser().getId());
        if (isNull(oAuth2AuthorizedClient)) {
            adapter.notifyUnauthorized(linkAuthorizationComponent.getUrl(event.getUser().getId(), CLIENT_REGISTRATION_ID));
            return;
        }

        linkAuthorizationComponent.authenticate(oAuth2AuthorizedClient);
        try {
            CommandInbound command = commandList.stream()
                .filter(c -> c.getData().getName() == commandName)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("CommandInbound with commandName=%s not found".formatted(commandName)));

            if (!devUserIdList.isEmpty() && !devUserIdList.contains(event.getUser().getId())) {
                notifyInDev(commandName, adapter);
                return;
            }

            if (adapter instanceof SlashCommandAdapter && command.isDeferReply()) {
                log.debug("Deferring command \"{}\"", commandName);
                event.deferReply().queue();
            }

            executeConsumer.accept(command);
        } catch (Exception e) {
            notifyError(commandName, errorText, adapter, e);
        }
    }

    private <A extends GenericAdapter<?, ?, ?>> void notifyError(Name commandName, String errorText, A adapter, Exception e) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(ERROR)
            .setTitle("Что-то пошло не так...")
            .setDescription(format(
                "Произошла ошибка с {0} команды \"{1}\". {2}", errorText,
                commandName, "Сообщите администратору точное время возникновения проблемы для быстрой диагностики проблемы."))));

        adapter.notify(messageData, true);
        log.error(format("Произошла ошибка с {0} команды \"{1}\"", errorText, commandName), e);
    }

    private <A extends GenericAdapter<?, ?, ?>> void notifyInDev(Name commandName, A adapter) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Бот немного занят")
            .setDescription(format("Бот сейчас не может выполнять команды кого-либо кроме _{0}_, так как активно разрабатывается/тестируется\n" +
                "Вы всегда (||нет||) можете воспользоваться старой версией бота <@905052906296852500>", devUserIdList.stream()
                .map("<@%s>"::formatted)
                .collect(joining(", "))))));

        adapter.notify(messageData, true);
        log.warn(format("Произошла попытка запуска команды \"{0}\" во время тестирования", commandName));
    }
}
