package ru.demetrious.deus.bot.adapter.output.anilist.dto.query;

import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.QueryAnilist;

/**
 * GraphQl query dto
 */
@AllArgsConstructor
public class ViewerAnilist implements QueryAnilist {
    @Override
    public String serialize() {
        return "Viewer{id}";
    }
}
