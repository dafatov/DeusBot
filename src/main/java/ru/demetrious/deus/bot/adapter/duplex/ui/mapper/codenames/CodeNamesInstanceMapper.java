package ru.demetrious.deus.bot.adapter.duplex.ui.mapper.codenames;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import java.util.Map;
import org.mapstruct.Condition;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesInstanceDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesPlayerDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.VoteDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.WordDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.TimerMapper;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Word;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesPlayerDto.TeamDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.VoteDto.SkipVoteDto;
import static ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.instance.VoteDto.WordVoteDto;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team.SPECTATOR;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote.SkipVote;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Vote.WordVote;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION, uses = {
    TimerMapper.class,
})
public interface CodeNamesInstanceMapper {
    CodeNamesInstanceDto map(CodeNamesInstance gameSession, @Context Player player, @Context boolean isFinished);

    CodeNamesPlayerDto map(CodeNamesPlayer player);

    @Mapping(target = "color", source = "color", conditionQualifiedByName = "needMapColor")
    WordDto map(Word word, @Context Player player, @Context boolean isFinished);

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
    default boolean needMapColor(Word word, @Context Player player, @Context boolean isFinished) {
        if (!(player instanceof CodeNamesPlayer codeNamesPlayer)) {
            throw new IllegalArgumentException("player must be instance of CodeNamesPlayer");
        }

        return nonNull(word.getRevealed()) || codeNamesPlayer.isCaptain() || codeNamesPlayer.getTeam() == SPECTATOR || isFinished;
    }
}
