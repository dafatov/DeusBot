package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.InstanceDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesInstanceDto;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION, uses = {
    CodeNamesInstanceMapper.class,
})
public interface InstanceMapper {
    @SubclassMapping(target = CodeNamesInstanceDto.class, source = CodeNamesInstance.class)
    @SuppressWarnings("UnmappedTargetProperties")
    InstanceDto<?> map(Instance<?, ?> gameSession, @Context Player player, @Context boolean isFinished);
}
