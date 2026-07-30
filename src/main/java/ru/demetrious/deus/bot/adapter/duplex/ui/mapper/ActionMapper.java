package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION, uses = {
    CodeNamesActionMapper.class,
})
public interface ActionMapper {
    @SubclassMapping(target = CodeNamesAction.class, source = CodeNamesActionDto.class)
    Action<?, ?, ?, ?> map(ActionDto actionDto);
}
