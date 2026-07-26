package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.TimerDto;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Timer;

@Mapper(imports = {Objects.class, Duration.class, Instant.class})
public interface TimerMapper {
    @Mapping(target = "timer", expression = "java(Objects.nonNull(timer.getFinish()) ? Duration.between(Instant.now(), timer.getFinish()) : null)")
    TimerDto map(Timer timer);
}
