package ru.demetrious.deus.bot.adapter.duplex.ui.mapper.crossward;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardActionDto.GetStateActionDto;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.GetStateAction;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION)
public interface CrossWardActionMapper {
    @SubclassMapping(target = GetStateAction.class, source = GetStateActionDto.class)
    CrossWardAction map(CrossWardActionDto actionDto);

    GetStateAction map(GetStateActionDto value);
}
