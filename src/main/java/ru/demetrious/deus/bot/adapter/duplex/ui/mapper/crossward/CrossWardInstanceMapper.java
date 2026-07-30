package ru.demetrious.deus.bot.adapter.duplex.ui.mapper.crossward;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardInstanceDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardPlayerDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.TimerMapper;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION, uses = {
    TimerMapper.class,
})
public interface CrossWardInstanceMapper {
    CrossWardInstanceDto map(CrossWardInstance gameSession, @Context Player player, @Context boolean isFinished);

    CrossWardPlayerDto map(CrossWardPlayer player);
}
