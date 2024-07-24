package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import ru.demetrious.deus.bot.domain.MessageData;

public interface GenericInteractionAdapter<Interaction> {
    void notify(MessageData messageData);

    AudioManager getAudioManager();

    VoiceChannel getVoiceChannel();

    String getGuildId();

    Interaction getInteraction();

    boolean isNotConnectedSameChannel();

    boolean isNotCanConnect();
}
