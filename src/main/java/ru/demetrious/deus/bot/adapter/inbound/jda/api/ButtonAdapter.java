package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageEmbed;

public interface ButtonAdapter extends GenericInteractionAdapter {
    MessageEmbed getEmbed(int index);

    void update(MessageData messageData);

    String getCustomId();
}
