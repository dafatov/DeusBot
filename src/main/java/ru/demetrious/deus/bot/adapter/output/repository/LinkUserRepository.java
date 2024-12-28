package ru.demetrious.deus.bot.adapter.output.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.demetrious.deus.bot.domain.LinkUser;
import ru.demetrious.deus.bot.domain.LinkUser.LinkUserKey;

@Repository
public interface LinkUserRepository extends JpaRepository<LinkUser, LinkUserKey> {
}
