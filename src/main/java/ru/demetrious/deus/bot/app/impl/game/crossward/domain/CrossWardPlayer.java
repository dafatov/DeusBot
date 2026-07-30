package ru.demetrious.deus.bot.app.impl.game.crossward.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class CrossWardPlayer extends Player {
    public CrossWardPlayer(String id, String name, String avatar) {
        super(id, name, avatar);
    }
}
