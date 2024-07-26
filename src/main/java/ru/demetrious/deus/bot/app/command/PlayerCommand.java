package ru.demetrious.deus.bot.app.command;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.SlashCommandAdapter;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.text.MessageFormat.format;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;

@Slf4j
@RequiredArgsConstructor
public abstract class PlayerCommand implements Command {
    private final Jukebox jukebox;

    protected Player getPlayer(String guildId) {
        return jukebox.getPlayer(guildId);
    }

    protected void notifyIsNotCanConnect(GenericInteractionAdapter genericInteractionAdapter) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Канал не тот")
            .setDescription("Мда.. шиза.. перепутать каналы это надо уметь")));

        genericInteractionAdapter.notify(messageData);
        log.warn("Не совпадают каналы");
    }

    protected void notifyUnbound(GenericInteractionAdapter genericInteractionAdapter) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Ты это.. Вселенной ошибся, чел.")
            .setDescription(format(
                "Типа знаешь вселенная расширяется, а твой мозг походу нет. Ну вышел ты за пределы размеров очереди или решил написать одинаковые индексы.\n" +
                    "Диапазон значений _от 1 по {0}_",
                getPlayer(genericInteractionAdapter.getGuildId()).getQueue().size()))));

        genericInteractionAdapter.notify(messageData);
        log.warn("Выход за пределы очереди");
    }

    protected void notifyIsNotPlaying(GenericInteractionAdapter genericInteractionAdapter) {
        notifyIsNotPlaying(List.of(), null, genericInteractionAdapter::notify);
    }

    protected void notifyIsNotPlaying(List<MessageComponent> components, String footer, Consumer<MessageData> queueConsumer) {
        MessageData messageData = new MessageData()
            .setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Мир музыки пуст")
                .setDescription("Может ли существовать мир без музыки? Каким бы он был...\nАх да! Таким, в котором сейчас живешь ты~~")
                .setFooter(footer)))
            .setComponents(components);

        queueConsumer.accept(messageData);
        log.warn("Плеер не играет");
    }

    protected void notifyIsLive(SlashCommandAdapter slashCommandAdapter) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Живая музыка")
            .setDescription("Зациклить то, что и так играет 24/7. Ты мой работодатель? Сорян, но не выйдет, а выйдет - уволюсь")));

        slashCommandAdapter.notify(messageData);
        log.warn("Играет стрим");
    }
}
