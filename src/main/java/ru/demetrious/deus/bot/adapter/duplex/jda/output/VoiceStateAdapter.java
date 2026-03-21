package ru.demetrious.deus.bot.adapter.duplex.jda.output;

import java.util.Collection;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.session.GetCurrentSessionListOutbound;
import ru.demetrious.deus.bot.domain.Session.SessionId;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

@Slf4j
@RequiredArgsConstructor
@Component
public class VoiceStateAdapter implements GetCurrentSessionListOutbound {
    private final JDA jda;

    @Override
    public Set<SessionId> getCurrentSessionList() {
        return jda.getGuilds().stream()
            .map(Guild::getMembers)
            .flatMap(Collection::stream)
            .filter(member -> nonNull(member.getVoiceState()) && member.getVoiceState().inAudioChannel())
            .map(member -> createSession(member.getGuild().getId(), member.getUser().getId()))
            .collect(toSet());
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private static SessionId createSession(String guildId, String userId) {
        return new SessionId()
            .setGuildId(guildId)
            .setUserId(userId);
    }
}
