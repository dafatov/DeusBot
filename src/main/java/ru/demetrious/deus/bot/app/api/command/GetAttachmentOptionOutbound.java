package ru.demetrious.deus.bot.app.api.command;

import java.util.Optional;
import ru.demetrious.deus.bot.domain.AttachmentOption;

@FunctionalInterface
public interface GetAttachmentOptionOutbound {
    Optional<AttachmentOption> getAttachmentOption(String name);
}
