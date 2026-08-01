package ru.demetrious.deus.bot.adapter.duplex.ui.mapper.crossward;

import java.util.List;
import java.util.Map;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardInstanceDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardPlayerDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.TimerMapper;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Cell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Position;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION, uses = {
    TimerMapper.class,
})
public interface CrossWardInstanceMapper {
    CrossWardInstanceDto map(CrossWardInstance gameSession, @Context Player player, @Context boolean isFinished);

    CrossWardPlayerDto map(CrossWardPlayer player);

    default List<CrossWardInstanceDto.PositionCellDto> map(Map<Position, Cell> value) {
        return value.entrySet().stream()
            .map(positionCellEntry -> map(positionCellEntry.getKey(), positionCellEntry.getValue()))
            .toList();
    }

    @Mapping(target = "x", source = "position.x")
    @Mapping(target = "y", source = "position.y")
    @Mapping(target = "letter", source = "cell.letter")
    CrossWardInstanceDto.PositionCellDto map(Position position, Cell cell);
}
