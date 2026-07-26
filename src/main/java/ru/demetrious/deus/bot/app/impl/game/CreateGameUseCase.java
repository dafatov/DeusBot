package ru.demetrious.deus.bot.app.impl.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.CreateGameInbound;
import ru.demetrious.deus.bot.app.impl.game.common.api.Gamebox;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

@RequiredArgsConstructor
@Component
public class CreateGameUseCase implements CreateGameInbound {
    private final Gamebox gamebox;

    @Override
    public String execute(Setting setting) {
        String key = gamebox.createNewGame(setting);

        gamebox.joinGame(key);
        return key;
    }
}
