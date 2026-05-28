package ru.demetrious.deus.bot.app.impl.game.codenames;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.codenames.DeleteCodeNamesGamePackInbound;
import ru.demetrious.deus.bot.app.api.game.codenames.DeleteCodeNamesGamePackOutbound;

@RequiredArgsConstructor
@Component
public class DeleteCodeNamesGamePackUseCase implements DeleteCodeNamesGamePackInbound {
    private final DeleteCodeNamesGamePackOutbound deleteCodeNamesGamePackOutbound;

    @Override
    public void execute(Long id) {
        deleteCodeNamesGamePackOutbound.deletePack(id);
    }
}
