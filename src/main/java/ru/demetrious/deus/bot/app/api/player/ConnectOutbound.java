package ru.demetrious.deus.bot.app.api.player;

import ru.demetrious.deus.bot.adapter.duplex.jda.config.AudioSendHandler;
import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;

public interface ConnectOutbound<A extends Interaction> extends HasEventOutbound {
    void connectPlayer(AudioSendHandler audioSendHandler);
}
