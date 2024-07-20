package ru.demetrious.deus.bot.app.player.api;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public interface Player {
    AudioSendHandler getAudioSendHandler();

    void add(String identifier);
}
