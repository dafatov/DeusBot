package ru.demetrious.deus.bot.adapter.output.freesteam;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.freesteam.mapper.FreebieMapper;
import ru.demetrious.deus.bot.app.api.freebie.GetFreebieListOutbound;
import ru.demetrious.deus.bot.domain.freebie.FreebieItem;

@RequiredArgsConstructor
@Component
public class FreeSteamAdapter implements GetFreebieListOutbound {
    private final FreeSteamClient freeSteamClient;
    private final FreebieMapper freebieMapper;

    @Override
    public List<FreebieItem> getFreebieList() {
        return freebieMapper.map(freeSteamClient.getFreebieRss().getChannel().getItem());
    }
}
