package ru.demetrious.deus.bot.adapter.inbound.jda.handler;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.factory.ModalAdapterFactory;
import ru.demetrious.deus.bot.adapter.inbound.jda.factory.SlashCommandAdapterFactory;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.text.MessageFormat.format;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.ERROR;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerAdapterImpl extends ListenerAdapter {
    private final SlashCommandAdapterFactory slashCommandAdapterFactory;
    private final ModalAdapterFactory modalAdapterFactory;
    private final List<Command> commandList;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        onInteraction(event, event.getName(), Command::execute, "запуском",
            () -> slashCommandAdapterFactory.create(event));
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
        Command command = commandList.stream()
            .filter(c -> c.getData().getName().equals(commandName))
            .findFirst()
            .orElseThrow();
        Adapter adapter = adapterFactorySupplier.get();

        if (adapter instanceof SlashCommandAdapter slashCommandAdapter && command.isDeferReply(slashCommandAdapter)) {
            log.debug("Deferring command \"{}\"", commandName);
            event.deferReply().queue();
        }

        try {
            executeConsumer.accept(command, adapter);
        } catch (Exception e) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(ERROR)
                .setTitle("Что-то пошло не так...")
                .setDescription(format(
                    "Произошла ошибка с {0} команды \"{1}\". {2}", errorText,
                    commandName, "Сообщите администратору точное время возникновения проблемы для быстрой диагностики проблемы."))));

            adapter.notify(messageData);
            log.error(format("Произошла ошибка с {0} \"{1}\"", errorText, commandName), e);
        }
    }
}
