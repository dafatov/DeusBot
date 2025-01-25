package ru.demetrious.deus.bot.adapter.output.repository.log;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.demetrious.deus.bot.domain.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {
    @Modifying
    @Query(value = "DELETE FROM log WHERE timestamp < :before RETURNING *", nativeQuery = true)
    List<Log> deleteAllByTimestampBefore(@Param("before") Instant before);
}
