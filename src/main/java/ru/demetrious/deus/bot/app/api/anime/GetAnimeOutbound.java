package ru.demetrious.deus.bot.app.api.anime;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import ru.demetrious.deus.bot.domain.Anime;

public interface GetAnimeOutbound {
    List<Anime> getAnimeList();

    byte[] getAnimeListXml() throws JsonProcessingException;
}
