package ru.demetrious.deus.bot.app.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.GenericInteractionAdapter;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

import static ru.demetrious.deus.bot.domain.MessageEmbed.ColorEnum.WARNING;

@Slf4j
@RequiredArgsConstructor
public abstract class PlayerCommand implements Command {
    protected final Jukebox jukebox;

    protected Player getPlayer(String guildId) {
        return jukebox.getPlayer(guildId);
    }

    protected void notifyIsNotCanConnect(GenericInteractionAdapter<?> genericInteractionAdapter) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Канал не тот")
            .setDescription("Мда.. шиза.. перепутать каналы это надо уметь")));

        genericInteractionAdapter.notify(messageData);
        log.warn("Не совпадают каналы");
    }

    protected void notifyIsNotPlaying(GenericInteractionAdapter<?> genericInteractionAdapter) {
        MessageData messageData = new MessageData().setEmbeds(List.of(new MessageEmbed()
            .setColor(WARNING)
            .setTitle("Мир музыки пуст")
            .setDescription("Может ли существовать мир без музыки? Каким бы он был...\nАх да! Таким, в котором сейчас живешь ты~~")));

        genericInteractionAdapter.notify(messageData);
        log.warn("Плеер не играет");
    }
}
