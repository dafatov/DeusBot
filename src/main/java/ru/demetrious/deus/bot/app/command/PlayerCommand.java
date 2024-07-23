package ru.demetrious.deus.bot.app.command;

import lombok.RequiredArgsConstructor;
import ru.demetrious.deus.bot.adapter.inbound.jda.api.CommandAdapter;
import ru.demetrious.deus.bot.app.command.api.Command;
import ru.demetrious.deus.bot.app.player.api.Jukebox;
import ru.demetrious.deus.bot.app.player.api.Player;

@RequiredArgsConstructor
public abstract class PlayerCommand implements Command {
    protected final Jukebox jukebox;

    protected Player getPlayer(CommandAdapter commandAdapter) {
        return jukebox.getPlayer(commandAdapter.getGuildId());
    }
}
