package ru.demetrious.deus.bot.app.impl.session;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.session.GetCurrentSessionListOutbound;
import ru.demetrious.deus.bot.app.api.session.GetSessionListOutbound;
import ru.demetrious.deus.bot.app.api.session.GuildVoiceSessionUpdateInbound;
import ru.demetrious.deus.bot.domain.Session;
import ru.demetrious.deus.bot.domain.Session.SessionId;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class SessionSynchronizeRunner {
    private final GetCurrentSessionListOutbound getCurrentSessionListOutbound;
    private final GetSessionListOutbound getSessionListOutbound;
    private final GuildVoiceSessionUpdateInbound guildVoiceSessionUpdateInbound;

    @PostConstruct
    public void synchronize() {
        Set<SessionId> currentSessionSet = getCurrentSessionListOutbound.getCurrentSessionList();
        List<Session> sessionList = getSessionListOutbound.getSessionList();

        sessionList.forEach(s -> {
            if (isNull(s.getFinish())) {
                updateSession(s.getId(), false);
            }

            if (currentSessionSet.remove(s.getId())) {
                updateSession(s.getId(), true);
            }
        });
        currentSessionSet.forEach(sessionId -> updateSession(sessionId, true));
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private void updateSession(SessionId sessionId, boolean isJoined) {
        guildVoiceSessionUpdateInbound.execute(sessionId.getGuildId(), sessionId.getUserId(), isJoined, true);
    }
}
