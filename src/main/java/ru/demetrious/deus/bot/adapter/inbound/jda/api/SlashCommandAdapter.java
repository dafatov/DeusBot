package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import java.util.Optional;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;

public interface SlashCommandAdapter extends GenericInteractionAdapter<SlashCommandInteraction> {
    String getLatency();

    Optional<String> getStringOption(String name);

    Optional<Message.Attachment> getAttachmentOption(String name);

    void showModal(Modal modal);
}
