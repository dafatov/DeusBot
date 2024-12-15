package ru.demetrious.deus.bot.app.api.player;

import ru.demetrious.deus.bot.app.api.interaction.Interaction;

@FunctionalInterface
public interface IsNotConnectedSameChannelOutbound<A extends Interaction> {
    boolean isNotConnectedSameChannel();
}
