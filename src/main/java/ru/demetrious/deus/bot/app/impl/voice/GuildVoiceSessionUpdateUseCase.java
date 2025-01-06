package ru.demetrious.deus.bot.app.impl.voice;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.session.GetSessionOutbound;
import ru.demetrious.deus.bot.app.api.session.SaveSessionOutbound;
import ru.demetrious.deus.bot.app.api.voice.GuildVoiceSessionUpdateInbound;
import ru.demetrious.deus.bot.domain.Session;

import static java.time.Instant.now;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Component
public class GuildVoiceSessionUpdateUseCase implements GuildVoiceSessionUpdateInbound {
    private final GetSessionOutbound getSessionOutbound;
    private final SaveSessionOutbound saveSessionOutbound;

    @Override
    public void execute(String guildId, String userId, boolean isJoined) {
        Session.SessionId sessionId = new Session.SessionId()
            .setGuildId(guildId)
            .setUserId(userId);
        Session session = getSessionOutbound.getSession(sessionId)
            .orElseGet(() -> new Session().setId(sessionId));
        Instant now = now();

        if (isJoined) {
            session.setStart(now).setFinish(null);
        } else {
            session.setStart(ofNullable(session.getStart()).orElse(now)).setFinish(now);
        }

        saveSessionOutbound.saveSession(session);
    }
}
