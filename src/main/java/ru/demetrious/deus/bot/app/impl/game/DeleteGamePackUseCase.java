package ru.demetrious.deus.bot.app.impl.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.DeleteGamePackInbound;
import ru.demetrious.deus.bot.app.api.game.DeleteGamePackOutbound;

@RequiredArgsConstructor
@Component
public class DeleteGamePackUseCase implements DeleteGamePackInbound {
    private final DeleteGamePackOutbound deleteGamePackOutbound;

    @Override
    public void execute(Long id) {
        deleteGamePackOutbound.deletePack(id);
    }
}
