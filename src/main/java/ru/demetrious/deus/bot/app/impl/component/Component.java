package ru.demetrious.deus.bot.app.impl.component;

import ru.demetrious.deus.bot.domain.MessageComponent;

public interface Component {
    MessageComponent get();

    MessageComponent update(String customId);
}
