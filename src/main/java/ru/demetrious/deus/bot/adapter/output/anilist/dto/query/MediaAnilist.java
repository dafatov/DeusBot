package ru.demetrious.deus.bot.adapter.output.anilist.dto.query;

import java.util.List;
import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.QueryAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaTypeAnilist;

import static java.util.stream.Collectors.joining;

@AllArgsConstructor
public class MediaAnilist implements QueryAnilist {
    private MediaTypeAnilist type;
    private List<Integer> idMalIn;

    @Override
    public String serialize() {
        String idIn = "[%s]".formatted(idMalIn.stream().map(String::valueOf).collect(joining(",")));

        return "media(type:%s,idMal_in:%s){id,idMal}".formatted(type, idIn);
    }
}
