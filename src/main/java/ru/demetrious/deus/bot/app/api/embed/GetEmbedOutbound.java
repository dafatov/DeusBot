package ru.demetrious.deus.bot.app.api.embed;

import ru.demetrious.deus.bot.domain.MessageEmbed;

@FunctionalInterface
public interface GetEmbedOutbound {
    MessageEmbed getEmbed(int index);
}
