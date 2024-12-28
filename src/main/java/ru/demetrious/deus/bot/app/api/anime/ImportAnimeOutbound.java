package ru.demetrious.deus.bot.app.api.anime;

import java.util.List;
import java.util.Map;
import ru.demetrious.deus.bot.domain.ImportAnimeContext;

@FunctionalInterface
public interface ImportAnimeOutbound {
    ImportAnimeContext execute(List<Map<String, String>> animeList);
}
