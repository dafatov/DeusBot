package ru.demetrious.deus.bot.adapter.output.repository.publicist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.demetrious.deus.bot.domain.Publicist;

@Repository
public interface PublicistRepository extends JpaRepository<Publicist, String> {
}
