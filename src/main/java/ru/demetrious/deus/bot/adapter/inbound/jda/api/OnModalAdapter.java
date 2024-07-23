package ru.demetrious.deus.bot.adapter.inbound.jda.api;

import java.util.List;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import ru.demetrious.deus.bot.domain.MessageData;

public interface OnModalAdapter {
    void notify(MessageData messageData);

    List<String> getValues();

    AudioManager getAudioManager();

    VoiceChannel getVoiceChannel();

    boolean isUnequalChannels();

    String getGuildId();
}
