package ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote.SkipVote;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote.WordVote;

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = SkipVote.class, name = "skip"),
    @Type(value = WordVote.class, name = "word")
})
public interface Vote {
    @Builder
    record SkipVote() implements Vote {
    }

    @Builder
    record WordVote(String word) implements Vote {
    }
}
