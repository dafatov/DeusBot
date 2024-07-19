package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import ru.demetrious.deus.bot.domain.MessageData;

public interface CommandAdapter {
    void notify(String content);

    void notify(MessageData content);

    String getLatency();
}
