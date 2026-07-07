package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import java.util.Map;
import org.mapstruct.Condition;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.GameSessionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.PlayerDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.VoteDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.WordDto;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Word;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.GameSessionDto.StateDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.PlayerDto.TeamDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.VoteDto.SkipVoteDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.VoteDto.WordVoteDto;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession.State.Phase.FINISHED;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player.Team.SPECTATOR;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote.SkipVote;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.vote.Vote.WordVote;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION)
public interface GameSessionMapper {
    GameSessionDto map(GameSession gameSession, @Context Player player, @Context Phase phase);

    @Mapping(target = "timer", expression = "java(java.util.Objects.nonNull(state.getTimer()) ? java.time.Duration.between(java.time.Instant.now(), state.getTimer()) : null)")
    StateDto mapState(State state);

    @Mapping(target = "color", source = "color", conditionQualifiedByName = "needMapColor")
    WordDto map(Word word, @Context Player player, @Context Phase phase);

    @Mapping(target = "disconnected", expression = "java(java.util.Objects.nonNull(player.getDisconnectCompletableFuture()))")
    PlayerDto map(Player player);

    TeamDto map(Team team);

    @SubclassMapping(target = SkipVoteDto.class, source = SkipVote.class)
    @SubclassMapping(target = WordVoteDto.class, source = WordVote.class)
    VoteDto map(Vote vote);

    SkipVoteDto map(SkipVote skipVote);

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    default Map<TeamDto, Integer> map(Multiset<Team> multiset) {
        return multiset.entrySet().stream().collect(toMap(entry -> map(entry.getElement()), Entry::getCount));
    }

    @Named("needMapColor")
    @Condition
    default boolean needMapColor(Word word, @Context Player player, @Context Phase phase) {
        return nonNull(word.getRevealed()) || player.isCaptain() || player.getTeam() == SPECTATOR || phase == FINISHED;
    }
}
