package ru.demetrious.deus.bot.adapter.duplex.ui.mapper.crossward;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Condition;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.EventDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardInstanceDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardInstanceDto.CellDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardInstanceDto.OrientationDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardInstanceDto.PositionCellDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardInstanceDto.WordDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardPlayerDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.instance.StateDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.TimerMapper;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Event;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION, uses = {
    TimerMapper.class,
    CrossWardActionMapper.class
})
public interface CrossWardInstanceMapper {
    CrossWardInstanceDto map(CrossWardInstance gameSession, @Context Player player, @Context boolean isFinished);

    @Mapping(target = "currentPlayer", source = "currentPlayer.id")
    StateDto map(State state);

    CrossWardPlayerDto map(CrossWardPlayer player);

    @Mapping(target = "x", source = "position.x")
    @Mapping(target = "y", source = "position.y")
    @Mapping(target = "letter", source = "cell.letter", conditionQualifiedByName = "needMapLetter")
    @Mapping(target = "words", source = "cell.words")
    @Mapping(target = "tags", source = "cell.tags")
    PositionCellDto map(Position position, Cell cell, @Context Player player, @Context boolean isFinished);

    OrientationDto map(Word.Orientation orientation);

    @Mapping(target = "background", ignore = true)
    @Mapping(target = "border", source = "owner.color")
    WordDto map(Word word);

    @Mapping(target = "x", source = "position.x")
    @Mapping(target = "y", source = "position.y")
    CellDto map(Cell cell);

    @Mapping(target = "issuerId", source = "issuer.id")
    EventDto<CrossWardActionDto> map(Event<CrossWardAction, CrossWardPlayer> event);

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    @Named("needMapLetter")
    @Condition
    default boolean needMapLetter(Cell cell, @Context Player player, @Context boolean isFinished) {
        if (!(player instanceof CrossWardPlayer crossWardPlayer)) {
            throw new IllegalArgumentException("player must be instance of CrossWardPlayer");
        }

        return cell.isRevealed() || isFinished || crossWardPlayer.isSpectator();
    }

    default List<PositionCellDto> map(Map<Position, Cell> value, @Context Player player, @Context boolean isFinished) {
        return value.entrySet().stream()
            .map(positionCellEntry -> map(positionCellEntry.getKey(), positionCellEntry.getValue(), player, isFinished))
            .toList();
    }

    default Map<OrientationDto, Integer> mapId(List<Word> value) {
        return value.stream().collect(toMap(w -> map(w.getOrientation()), Word::getOrder));
    }

    default Map<Integer, WordDto> map(List<Word> value) {
        return value.stream().collect(toMap(Word::getOrder, this::map));
    }

    default String mapColor(@Nullable Color color) {
        return ofNullable(color)
            .map(Color::getRGB)
            .map(g -> g & 0x00FFFFFF)
            .map(g -> format("#%06x", g))
            .orElse(null);
    }

    default String mapToId(CrossWardPlayer player) {
        return player.getId();
    }
}
