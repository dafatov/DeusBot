package ru.demetrious.deus.bot.adapter.output.repository.pack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.demetrious.deus.bot.domain.game.Pack;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {
}
