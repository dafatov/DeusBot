package ru.demetrious.deus.bot.adapter.output.repository.audit;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.demetrious.deus.bot.domain.Audit;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Audit.AuditId> {
    List<Audit> findByAuditId_GuildIdAndAuditId_Type(String guildId, Audit.Type type);

    void deleteAllByAuditId_Type(Audit.Type type);
}
