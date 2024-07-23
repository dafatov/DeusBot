package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import java.util.List;
import ru.demetrious.deus.bot.domain.MessageData;

public interface OnModalAdapter {
    void notify(MessageData messageData);

    List<String> getValues();
}
