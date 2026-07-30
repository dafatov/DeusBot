package ru.demetrious.deus.bot.app.impl.game.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.concurrent.CompletableFuture;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static java.util.Objects.nonNull;
import static ru.demetrious.deus.bot.domain.game.GameType.CODE_NAMES;
import static ru.demetrious.deus.bot.domain.game.GameType.CROSS_WARD;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "game")
@JsonSubTypes({
    @Type(value = CodeNamesPlayer.class, name = CODE_NAMES),
    @Type(value = CrossWardPlayer.class, name = CROSS_WARD),
})
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@Data
public abstract class Player {
    private final String id;
    private final String name;
    private final String avatar;
    private CompletableFuture<Void> disconnectCompletableFuture;

    public boolean isDisconnected() {
        return nonNull(disconnectCompletableFuture);
    }
}
