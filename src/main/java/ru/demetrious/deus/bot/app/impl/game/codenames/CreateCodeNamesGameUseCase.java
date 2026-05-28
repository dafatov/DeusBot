package ru.demetrious.deus.bot.app.impl.game.codenames;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.game.codenames.CreateCodeNamesGameInbound;
import ru.demetrious.deus.bot.app.impl.game.codenames.api.Gamebox;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Setting;

@RequiredArgsConstructor
@Component
public class CreateCodeNamesGameUseCase implements CreateCodeNamesGameInbound {
    private final Gamebox gamebox;

    @Override
    public String execute(Setting setting) {
        String key = gamebox.createNewGame(setting);

        gamebox.joinGame(key);
        return key;
    }
}
