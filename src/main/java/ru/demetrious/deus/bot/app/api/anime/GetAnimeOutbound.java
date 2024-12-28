package ru.demetrious.deus.bot.app.api.anime;

import java.util.Map;

@FunctionalInterface
public interface GetAnimeOutbound {
    Map<String, Object> getAnimeList();
}
