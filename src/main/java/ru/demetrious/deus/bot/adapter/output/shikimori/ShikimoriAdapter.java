package ru.demetrious.deus.bot.adapter.output.shikimori;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.anime.GetAnimeOutbound;

@RequiredArgsConstructor
@Component
public class ShikimoriAdapter implements GetAnimeOutbound {
    private final ShikimoriClient shikimoriClient;

    @Override
    public Map<String, Object> getAnimeList() {
        return shikimoriClient.getAnimeList(shikimoriClient.getMe().getNickname());
    }
}
