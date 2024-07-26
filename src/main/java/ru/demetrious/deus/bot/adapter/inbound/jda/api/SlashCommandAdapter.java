package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import java.util.Optional;
import ru.demetrious.deus.bot.domain.AttachmentOption;
import ru.demetrious.deus.bot.domain.ModalData;

public interface SlashCommandAdapter extends GenericInteractionAdapter {
    String getLatency();

    Optional<String> getStringOption(String name);

    Optional<AttachmentOption> getAttachmentOption(String name);

    void showModal(ModalData modal);

    Optional<Integer> getIntegerOption(String name);
}
