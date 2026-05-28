package ru.demetrious.deus.bot.app.api.game.codenames;

import java.util.List;
import ru.demetrious.deus.bot.domain.game.Pack;

@FunctionalInterface
public interface GetCodeNamesGamePacksOutbound {
    List<Pack> getPacks();
}
