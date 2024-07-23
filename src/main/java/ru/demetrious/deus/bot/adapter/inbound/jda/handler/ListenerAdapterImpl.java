package ru.demetrious.deus.bot.adapter.inbound.jda.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.OnModalAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.factory.CommandAdapterFactory;
import ru.demetrious.deus.bot.adapter.inbound.jda.factory.OnModalAdapterFactory;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.ERROR;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerAdapterImpl extends ListenerAdapter {
    private final CommandAdapterFactory commandAdapterFactory;
    private final OnModalAdapterFactory onModalAdapterFactory;
    private final List<Command> commandList;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        Command command = commandList.stream()
            .filter(c -> c.getData().getName().equals(commandName))
            .findFirst()
            .orElseThrow();
        CommandAdapter commandAdapter = commandAdapterFactory.create(event);

        if (command.isDeferReply(commandAdapter)) {
            log.debug("Deferring command \"{}\"", commandName);
            event.deferReply().queue();
        }

        try {
            command.execute(commandAdapter);
        } catch (Exception e) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(ERROR)
                .setTitle("Что-то пошло не так...")
                .setDescription("Произошла ошибка с запуском команды \"" + commandName +
                    "\". Сообщите администратору идентификатор записи в логах для быстрой диагностики проблемы.")));

            commandAdapter.notify(messageData);
            log.error("Произошла ошибка с запуском команды \"" + commandName + "\"", e);
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String commandName = event.getModalId().split(" ")[0];
        Command command = commandList.stream()
            .filter(c -> c.getData().getName().equals(commandName))
            .findFirst()
            .orElseThrow();
        OnModalAdapter onModalAdapter = onModalAdapterFactory.create(event);

        event.deferReply().queue();
        try {
            command.onModal(onModalAdapter);
        } catch (Exception e) {
            MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
                .setColor(ERROR)
                .setTitle("Что-то пошло не так...")
                .setDescription("Произошла ошибка с обработкой события модального окна команды \"" + commandName +
                    "\". Сообщите администратору идентификатор записи в логах для быстрой диагностики проблемы.")));

            onModalAdapter.notify(messageData);
            log.error("Произошла ошибка с обработкой события модального окна команды \"" + commandName + "\"", e);
        }
    }
}
