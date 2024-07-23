package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import java.util.Optional;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.managers.AudioManager;
import ru.demetrious.deus.bot.domain.MessageData;

public interface CommandAdapter {
    void notify(MessageData content);

    String getLatency();

    VoiceChannel getVoiceChannel();

    AudioManager getAudioManager();

    String getGuildId();

    Optional<String> getStringOption(String name);

    Optional<Message.Attachment> getAttachmentOption(String name);

    boolean isUnequalChannels();

    void showModal(Modal modal);
}
