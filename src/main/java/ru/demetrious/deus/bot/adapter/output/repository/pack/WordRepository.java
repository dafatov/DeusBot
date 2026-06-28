package ru.demetrious.deus.bot.adapter.output.repository.pack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.demetrious.deus.bot.domain.game.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, String> {
}
