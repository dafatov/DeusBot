package ru.demetrious.deus.bot.adapter.output.anilist.dto;

public interface MutationAnilist {
    String getName();

    default String serialize() {

        return "";
    }
}
