package ru.demetrious.deus.bot.adapter.output.reverse1999;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.reverse1999.dto.SummonsDto;
import ru.demetrious.deus.bot.adapter.output.reverse1999.mapper.SummonedCharacterMapper;
import ru.demetrious.deus.bot.app.api.character.GetReverseSummonedCharacterListOutbound;
import ru.demetrious.deus.bot.domain.Pull;

@Slf4j
@RequiredArgsConstructor
@Component
public class Reverse1999Adapter implements GetReverseSummonedCharacterListOutbound {
    private final Reverse1999Client reverse1999Client;
    private final SummonedCharacterMapper summonedCharacterMapper;

    @Override
    public Optional<List<Pull>> getReverseSummonedCharacterList(URI uri) {
        return reverse1999Client.getSummons(uri)
            .map(SummonsDto::data)
            .map(SummonsDto.Data::pageData)
            .map(summonedCharacterMapper::map);
    }
}
