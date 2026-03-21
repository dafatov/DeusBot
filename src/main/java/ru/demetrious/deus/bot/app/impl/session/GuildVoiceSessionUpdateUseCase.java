package ru.demetrious.deus.bot.app.impl.session;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.session.GetSessionOutbound;
import ru.demetrious.deus.bot.app.api.session.GuildVoiceSessionUpdateInbound;
import ru.demetrious.deus.bot.app.api.session.SaveSessionOutbound;
import ru.demetrious.deus.bot.domain.Session;

import static java.time.Instant.now;
import static ru.demetrious.deus.bot.domain.Session.SessionId;

@RequiredArgsConstructor
@Component
public class GuildVoiceSessionUpdateUseCase implements GuildVoiceSessionUpdateInbound {
    private final GetSessionOutbound getSessionOutbound;
    private final SaveSessionOutbound saveSessionOutbound;

    @Override
    public void execute(String guildId, String userId, boolean isJoined) {
        execute(guildId, userId, isJoined, false);
    }

    @Override
    public void execute(String guildId, String userId, boolean isJoined, boolean isForced) {
        SessionId sessionId = new SessionId()
            .setGuildId(guildId)
            .setUserId(userId);
        Session session = getSessionOutbound.getSession(sessionId)
            .orElseGet(() -> new Session().setId(sessionId));
        Instant now = now();

        if (isJoined) {
            session.start(now, isForced);
        } else {
            session.finish(now, isForced);
        }

        saveSessionOutbound.saveSession(session);
    }
}
