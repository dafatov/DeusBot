package ru.demetrious.deus.bot.adapter.output.repository.session;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demetrious.deus.bot.app.api.session.GetGuildSessionListOutbound;
import ru.demetrious.deus.bot.app.api.session.GetSessionListOutbound;
import ru.demetrious.deus.bot.app.api.session.GetSessionOutbound;
import ru.demetrious.deus.bot.app.api.session.SaveSessionOutbound;
import ru.demetrious.deus.bot.domain.Session;
import ru.demetrious.deus.bot.domain.Session.SessionId;

@Transactional
@RequiredArgsConstructor
@Component
public class SessionAdapter implements GetGuildSessionListOutbound, GetSessionOutbound, SaveSessionOutbound, GetSessionListOutbound {
    private final SessionRepository sessionRepository;

    @Override
    public Optional<Session> getSession(SessionId sessionId) {
        return sessionRepository.findById(sessionId);
    }

    @Override
    public void saveSession(Session session) {
        sessionRepository.save(session);
    }

    @Override
    public List<Session> getGuildSessionList(String guildId) {
        return sessionRepository.findById_GuildId(guildId);
    }

    @Override
    public List<Session> getSessionList() {
        return sessionRepository.findAll();
    }
}
