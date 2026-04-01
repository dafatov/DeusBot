package ru.demetrious.deus.bot.adapter.duplex.jda.input;

import java.net.URI;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.AutocompleteAdapter;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.BaseAdapter;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.ButtonAdapter;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.GenericAdapter;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.ModalAdapter;
import ru.demetrious.deus.bot.adapter.duplex.jda.output.SlashCommandAdapter;
import ru.demetrious.deus.bot.app.api.command.CommandInbound;
import ru.demetrious.deus.bot.app.api.message.MessageReceivedInbound;
import ru.demetrious.deus.bot.app.api.player.ClearGuildPlayerInbound;
import ru.demetrious.deus.bot.app.api.player.LeaveIfAloneInbound;
import ru.demetrious.deus.bot.app.api.session.GuildVoiceSessionUpdateInbound;
import ru.demetrious.deus.bot.domain.CommandData.Name;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;
import ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static net.dv8tion.jda.api.entities.channel.ChannelType.PRIVATE;
import static ru.demetrious.deus.bot.app.api.command.CommandInbound.Type;
import static ru.demetrious.deus.bot.app.api.command.CommandInbound.Type.BUTTON;
import static ru.demetrious.deus.bot.app.api.command.CommandInbound.Type.COMMAND;
import static ru.demetrious.deus.bot.app.api.command.CommandInbound.Type.MODAL;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.ERROR;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.DISCORD_REGISTRATION_ID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerAdapter extends net.dv8tion.jda.api.hooks.ListenerAdapter {
    private final AuthorizationComponent authorizationComponent;
    private final List<CommandInbound> commandInboundList;
    private final SlashCommandAdapter slashCommandAdapter;
    private final ModalAdapter modalAdapter;
    private final ButtonAdapter buttonAdapter;
    private final AutocompleteAdapter autocompleteAdapter;
    private final GuildVoiceSessionUpdateInbound guildVoiceSessionUpdateInbound;
    private final MessageReceivedInbound messageReceivedInbound;
    private final ClearGuildPlayerInbound clearGuildPlayerInbound;
    private final LeaveIfAloneInbound leaveIfAloneInbound;

    @Value("${devs.ids:}")
    private List<String> devUserIdList;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        onInteraction(event, CommandInbound::execute, "запуском", slashCommandAdapter);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        onInteraction(event, CommandInbound::onButton, "обработкой события кнопки", buttonAdapter);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        onInteraction(event, CommandInbound::onAutocomplete, (_, _) -> replyEmptyChoices(event), _ -> replyEmptyChoices(event), autocompleteAdapter);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        onInteraction(event, CommandInbound::onModal, "обработкой события модального окна", modalAdapter);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        messageReceivedInbound.execute(event.getGuild().getId(), event.getAuthor().getId());
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        Guild guild = event.getGuild();
        String guildId = guild.getId();
        String userId = event.getMember().getUser().getId();

        if (isNull(event.getOldValue()) || isNull(event.getNewValue())) {
            guildVoiceSessionUpdateInbound.execute(guildId, userId, nonNull(event.getNewValue()), false);
        }

        if (event.getJDA().getSelfUser().getId().equals(userId) && isNull(event.getNewValue())) {
            clearGuildPlayerInbound.execute(guildId);
        }

        leaveIfAloneInbound.execute(guildId, ofNullable(guild.getAudioManager().getConnectedChannel())
            .map(c -> c.getMembers().stream().map(Member::getUser).allMatch(User::isBot))
            .orElse(false));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private <E, A extends BaseAdapter<E, ?>> void onInteraction(@NotNull E event,
                                                                @NotNull Consumer<CommandInbound> executeConsumer,
                                                                @NotNull BiConsumer<Name, Exception> errorConsumer,
                                                                @NotNull Consumer<Pair<String, URI>> unauthorizedConsumer,
                                                                @NotNull A adapter) {
        adapter.setEvent(event);

        try {
            if (authorizationComponent.authorize(DISCORD_REGISTRATION_ID, adapter.getUserId()).isEmpty()) {
                unauthorizedConsumer.accept(authorizationComponent.getData(adapter.getUserId(), DISCORD_REGISTRATION_ID));
                return;
            }

            Name commandName = adapter.getCommandName();

            try {
                CommandInbound command = commandInboundList.stream()
                    .filter(c -> c.getData().getName() == commandName)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("CommandInbound with commandName=%s not found".formatted(commandName)));

                executeConsumer.accept(command);
            } catch (Exception e) {
                errorConsumer.accept(commandName, e);
            }
        } catch (Exception e) {
            errorConsumer.accept(null, e);
        } finally {
            adapter.removeEvent();
        }
    }

    private <E extends IReplyCallback, A extends GenericAdapter<?, E, ?>> void onInteraction(@NotNull E event,
                                                                                             @NotNull Consumer<CommandInbound> executeConsumer,
                                                                                             String errorText,
                                                                                             @NotNull A adapter) {
        onInteraction(event, commandInbound -> {
            if (PRIVATE.equals(event.getChannelType())) {
                notifyPrivateChannel(commandInbound.getData().getName(), adapter);
                return;
            }

            if (!devUserIdList.isEmpty() && !devUserIdList.contains(adapter.getUserId())) {
                notifyInDev(commandInbound.getData().getName(), adapter);
                return;
            }

            if (commandInbound.isDefer(getEventType(adapter))) {
                log.debug("Deferring command \"{}\"", commandInbound.getData().getName());
                adapter.defer();
            }

            executeConsumer.accept(commandInbound);
        }, (commandName, e) -> notifyError(commandName, errorText, adapter, e), adapter::notifyUnauthorized, adapter);
    }

    private Type getEventType(@NotNull GenericAdapter<?, ?, ?> adapter) {
        return switch (adapter) {
            case SlashCommandAdapter ignored -> COMMAND;
            case ButtonAdapter ignored -> BUTTON;
            case ModalAdapter ignored -> MODAL;
            default -> throw new IllegalStateException("Unexpected value: " + adapter.getClass());
        };
    }

    private <A extends GenericAdapter<?, ?, ?>> void notifyError(Name commandName, String errorText, A adapter, Exception e) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(ERROR)
            .setTitle("Что-то пошло не так...")
            .setDescription(format(
                "Произошла ошибка с {0} команды \"{1}\". {2}", errorText,
                commandName, "Сообщите администратору точное время возникновения проблемы для быстрой диагностики проблемы."))));

        adapter.notify(messageData, true);
        log.error("Произошла ошибка с {} команды \"{}\"", errorText, commandName, e);
    }

    private <A extends GenericAdapter<?, ?, ?>> void notifyPrivateChannel(Name commandName, A adapter) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(ERROR)
            .setTitle("У нас так не принято")
            .setDescription("Из приватного канала бот не хочет выполнять команды. Увы!")));

        adapter.notify(messageData, true);
        log.warn("Произошла попытка запуска команды \"{}\" из приватного канала", commandName);
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
        log.warn("Произошла попытка запуска команды \"{}\" во время тестирования", commandName);
    }

    private void replyEmptyChoices(@NotNull CommandAutoCompleteInteractionEvent event) {
        event.replyChoices(List.of()).queue();
    }
}
