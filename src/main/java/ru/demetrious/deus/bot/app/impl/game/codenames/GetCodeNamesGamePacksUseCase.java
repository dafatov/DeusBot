package ru.demetrious.deus.bot.app.impl.game.codenames;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.codenames.GetCodeNamesGamePacksInbound;
import ru.demetrious.deus.bot.app.api.game.codenames.GetCodeNamesGamePacksOutbound;
import ru.demetrious.deus.bot.domain.game.Pack;

@RequiredArgsConstructor
@Component
public class GetCodeNamesGamePacksUseCase implements GetCodeNamesGamePacksInbound {
    private final GetCodeNamesGamePacksOutbound getCodeNamesGamePacksOutbound;

    @Override
    public List<Pack> execute() {
        return getCodeNamesGamePacksOutbound.getPacks();
    }
}
