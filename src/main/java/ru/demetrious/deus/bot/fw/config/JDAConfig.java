package ru.demetrious.deus.bot.fw.config;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.text.MessageFormat.format;
import static net.dv8tion.jda.api.JDABuilder.createDefault;
import static net.dv8tion.jda.api.OnlineStatus.DO_NOT_DISTURB;
import static net.dv8tion.jda.api.entities.Activity.playing;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.EMOJI;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.SCHEDULED_EVENTS;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.STICKER;

@RequiredArgsConstructor
@Configuration
public class JDAConfig {
    private final ListenerAdapter listenerAdapter;

    @Value("${discord.token}")
    private String token;
    @Value("${git.build.version:?.?.?}-${git.commit.id.abbrev:??????}")
    private String version;

    @Bean
    public JDA jda() throws InterruptedException {
        return createDefault(token)
            .disableCache(EMOJI, STICKER, SCHEDULED_EVENTS)
            .setEnabledIntents(GUILD_MEMBERS, GUILD_MESSAGES, GUILD_VOICE_STATES, MESSAGE_CONTENT)
            .setActivity(playing(format("/help | v{0}", version)))
            .setStatus(DO_NOT_DISTURB)
            .addEventListeners(listenerAdapter)
            .build()
            .awaitReady();
    }
}
