package ru.demetrious.deus.bot.app.impl.game.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "game")
@JsonSubTypes({
    @Type(value = CodeNamesInstance.class, name = CODE_NAMES)
})
@EqualsAndHashCode(of = "key")
@Data
public abstract class Instance<S extends Setting, P extends Player> {
    private final String key;
    private final String hostId;
    private final S setting;
    private final Set<P> playerList = new LinkedHashSet<>();
    private final Timer timer = new Timer();

    public abstract boolean isFinished();
}
