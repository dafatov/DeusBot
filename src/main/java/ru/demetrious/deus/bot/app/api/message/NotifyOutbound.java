package ru.demetrious.deus.bot.app.api.message;

import java.net.URI;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.tuple.Pair;
import ru.demetrious.deus.bot.app.api.event.HasEventOutbound;
import ru.demetrious.deus.bot.app.api.interaction.Interaction;
import ru.demetrious.deus.bot.domain.MessageData;

public interface NotifyOutbound<I extends Interaction> extends HasEventOutbound {
    int MAX_ATTACHMENTS = 10;
    int DESCRIPTION_MAX_LENGTH = MessageEmbed.DESCRIPTION_MAX_LENGTH;

    void notify(MessageData messageData, boolean isEphemeral);

    void notify(MessageData messageData);

    void notifyUnauthorized(Pair<String, URI> authorizeData);
}
