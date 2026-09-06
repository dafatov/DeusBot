package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.codenames.CodeNamesActionMapper;
import ru.demetrious.deus.bot.adapter.duplex.ui.mapper.crossward.CrossWardActionMapper;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION, uses = {
    CodeNamesActionMapper.class,
    CrossWardActionMapper.class,
})
public interface ActionMapper {
    @SubclassMapping(target = CodeNamesAction.class, source = CodeNamesActionDto.class)
    @SubclassMapping(target = CrossWardAction.class, source = CrossWardActionDto.class)
    Action<?, ?, ?, ?> map(ActionDto actionDto);
}
