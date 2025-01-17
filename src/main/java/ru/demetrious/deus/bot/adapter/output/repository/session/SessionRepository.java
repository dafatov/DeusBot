package ru.demetrious.deus.bot.adapter.output.repository.session;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.demetrious.deus.bot.domain.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Session.SessionId> {
    List<Session> findById_GuildId(String guildId);
}
