package ru.demetrious.deus.bot.app.impl.game;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.GetGamePacksInbound;
import ru.demetrious.deus.bot.app.api.game.GetGamePacksOutbound;
import ru.demetrious.deus.bot.domain.game.Pack;

@RequiredArgsConstructor
@Component
public class GetGamePacksUseCase implements GetGamePacksInbound {
    private final GetGamePacksOutbound getGamePacksOutbound;

    @Override
    public List<Pack> execute() {
        return getGamePacksOutbound.getPacks();
    }
}
