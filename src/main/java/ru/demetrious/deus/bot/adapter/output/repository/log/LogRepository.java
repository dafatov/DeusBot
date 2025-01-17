package ru.demetrious.deus.bot.adapter.output.repository.log;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.demetrious.deus.bot.domain.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {
    List<Log> deleteAllByTimestampBefore(Instant before);
}
