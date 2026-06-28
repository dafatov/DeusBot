package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.ErrorDto;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION)
public interface ErrorMapper {
    @Mapping(target = "message", source = "message")
    ErrorDto map(Exception exception);
}
