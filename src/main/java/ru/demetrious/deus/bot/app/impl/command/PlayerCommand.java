package ru.demetrious.deus.bot.app.impl.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.demetrious.deus.bot.app.api.command.CommandInbound;
import ru.demetrious.deus.bot.app.api.guild.GetGuildIdOutbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.impl.player.api.Jukebox;
import ru.demetrious.deus.bot.app.impl.player.api.Player;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static java.text.MessageFormat.format;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;
import static ru.demetrious.deus.bot.utils.BeanUtils.b;

@Slf4j
@RequiredArgsConstructor
public abstract class PlayerCommand implements CommandInbound {
    @Autowired
    private Jukebox jukebox;
    @Autowired
    private List<NotifyOutbound<?>> notifyOutbound;
    @Autowired
    private List<GetGuildIdOutbound<?>> getGuildIdOutbound;

    protected Player getPlayer() {
        return jukebox.getPlayer(b(getGuildIdOutbound).getGuildId());
    }

    protected void notifyIsNotCanConnect() {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Канал не тот")
            .setDescription("Мда.. шиза.. перепутать каналы это надо уметь")));

        b(notifyOutbound).notify(messageData);
        log.warn("Не совпадают каналы");
    }

    protected void notifyUnbound() {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Ты это.. Вселенной ошибся, чел.")
            .setDescription(format(
                "Типа знаешь вселенная расширяется, а твой мозг походу нет. Ну вышел ты за пределы размеров очереди или решил написать одинаковые индексы.\n" +
                    "Диапазон значений _от 1 по {0}_",
                emptyIfNull(getPlayer().getQueue().getData()).size()))));

        b(notifyOutbound).notify(messageData);
        log.warn("Выход за пределы очереди");
    }

    protected void notifyIsNotPlaying() {
        notifyIsNotPlaying(List.of(), null);
    }

    protected void notifyIsNotPlaying(List<MessageComponent> components, String footer) {
        MessageData messageData = new MessageData()
            .setEmbeds(List.of(new MessageEmbed()
                .setColor(WARNING)
                .setTitle("Мир музыки пуст")
                .setDescription("Может ли существовать мир без музыки? Каким бы он был...\nАх да! Таким, в котором сейчас живешь ты~~")
                .setFooter(footer)))
            .setComponents(components);

        b(notifyOutbound).notify(messageData);
        log.warn("Плеер не играет");
    }

    protected void notifyIsLive() {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Живая музыка")
            .setDescription("Зациклить то, что и так играет 24/7. Ты мой работодатель? Сорян, но не выйдет, а выйдет - уволюсь")));

        b(notifyOutbound).notify(messageData);
        log.warn("Играет стрим");
    }
}
