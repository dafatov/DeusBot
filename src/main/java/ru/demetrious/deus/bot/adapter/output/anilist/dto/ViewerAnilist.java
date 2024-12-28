package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import lombok.AllArgsConstructor;

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
