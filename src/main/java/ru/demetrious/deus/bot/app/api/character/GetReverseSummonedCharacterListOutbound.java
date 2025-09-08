package ru.demetrious.deus.bot.app.api.character;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import ru.demetrious.deus.bot.domain.Pull;

@FunctionalInterface
public interface GetReverseSummonedCharacterListOutbound {
    Optional<List<Pull>> getReverseSummonedCharacterList(URI uri);
}
