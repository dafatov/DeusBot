package ru.demetrious.deus.bot.app.api.anime;

import java.util.List;
import ru.demetrious.deus.bot.domain.Anime;
import ru.demetrious.deus.bot.domain.ImportAnimeContext;

@FunctionalInterface
public interface ImportAnimeOutbound {
    ImportAnimeContext execute(List<Anime> animeList, Integer userId);
}
