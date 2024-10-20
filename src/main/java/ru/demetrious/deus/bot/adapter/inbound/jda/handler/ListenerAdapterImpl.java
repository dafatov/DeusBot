package ru.demetrious.deus.bot.adapter.inbound.jda.handler;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.factory.ButtonAdapterFactory;
import ru.demetrious.deus.bot.adapter.inbound.jda.factory.ModalAdapterFactory;
import ru.demetrious.deus.bot.adapter.inbound.jda.factory.SlashCommandAdapterFactory;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.ERROR;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerAdapterImpl extends ListenerAdapter {
    private final SlashCommandAdapterFactory slashCommandAdapterFactory;
    private final ModalAdapterFactory modalAdapterFactory;
    private final ButtonAdapterFactory buttonAdapterFactory;
    private final List<Command> commandList;

    @Value("${devs.ids:}")
    private List<String> devUserIdList;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        onInteraction(event, event.getName(), Command::execute, "запуском",
            () -> slashCommandAdapterFactory.create(event));
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String commandName = Optional.ofNullable(event.getMessage().getInteraction())
            .map(Message.Interaction::getName)
            .map(cN -> cN.split(" "))
            .map(split -> split[0])
            .orElse(null);

        onInteraction(event, commandName, Command::onButton, "обработкой события кнопки",
            () -> buttonAdapterFactory.create(event));
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        onInteraction(event, event.getModalId().split(" ")[0], Command::onModal, "обработкой события модального окна",
            () -> modalAdapterFactory.create(event));
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private <Event extends GenericInteractionCreateEvent & IReplyCallback,
        Adapter extends GenericInteractionAdapter> void onInteraction(@NotNull Event event,
                                                                      String commandName,
                                                                      BiConsumer<Command, Adapter> executeConsumer,
                                                                      String errorText,
                                                                      Supplier<Adapter> adapterFactorySupplier) {
        Adapter adapter = adapterFactorySupplier.get();

        try {
            Command command = commandList.stream()
                .filter(c -> c.getData().getName().equals(commandName))
                .findFirst()
                .orElseThrow();

            if (!devUserIdList.isEmpty() && !devUserIdList.contains(event.getUser().getId())) {
                notifyInDev(commandName, adapter);
                return;
            }

            if (adapter instanceof SlashCommandAdapter slashCommandAdapter && command.isDeferReply(slashCommandAdapter)) {
                log.debug("Deferring command \"{}\"", commandName);
                event.deferReply().queue();
            }

            executeConsumer.accept(command, adapter);
        } catch (Exception e) {
            notifyError(commandName, errorText, adapter, e);
        }
    }

    private <Adapter extends GenericInteractionAdapter> void notifyError(String commandName, String errorText, Adapter adapter, Exception e) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(ERROR)
            .setTitle("Что-то пошло не так...")
            .setDescription(format(
                "Произошла ошибка с {0} команды \"{1}\". {2}", errorText,
                commandName, "Сообщите администратору точное время возникновения проблемы для быстрой диагностики проблемы."))));

        adapter.notify(messageData, true);
        log.error(format("Произошла ошибка с {0} команды \"{1}\"", errorText, commandName), e);
    }

    private <Adapter extends GenericInteractionAdapter> void notifyInDev(String commandName, Adapter adapter) {
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
