package ru.demetrious.deus.bot.app.impl.game.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Synchronized;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static org.apache.commons.collections4.list.SetUniqueList.setUniqueList;
import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "game")
@JsonSubTypes({
    @Type(value = CodeNamesInstance.class, name = CODE_NAMES),
    @Type(value = CrossWardInstance.class, name = CROSS_WARD),
})
@EqualsAndHashCode(of = "key")
@Data
public abstract class Instance<S extends Setting, P extends Player> {
    private final String key;
    private final S setting;
    private final List<P> playerList = setUniqueList(new ArrayList<>());
    private final List<Event> history = new ArrayList<>();
    private final Timer timer = new Timer();
    private String hostId;

    public Instance(String key, S setting, String hostId) {
        this.key = key;
        this.setting = setting;
        this.hostId = hostId;
    }

    public abstract boolean isFinished();

    public void removePlayer(P player) {
        playerList.remove(player);
    }

    @Synchronized
    public <E extends Event> E addEvent(Function<Integer, E> eventFunction) {
        E event = eventFunction.apply(history.size());

        history.addFirst(event);
        return event;
    }
}
