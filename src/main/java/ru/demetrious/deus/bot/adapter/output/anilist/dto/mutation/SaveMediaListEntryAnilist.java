package ru.demetrious.deus.bot.adapter.output.anilist.dto.mutation;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.MutationAnilist;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist;

import static java.lang.String.join;
import static java.util.Objects.nonNull;

/**
 * GraphQl mutation dto
 */
@AllArgsConstructor
public class SaveMediaListEntryAnilist implements MutationAnilist {
    private MediaListStatusAnilist status;
    private Integer mediaId;
    private Integer progress;
    private Double score;
    private Integer repeat;

    @Override
    public String serialize() {
        Set<String> fields = new HashSet<>();

        if (nonNull(status)) {
            fields.add("status:" + status);
        }
        fields.add("mediaId:" + mediaId);
        fields.add("progress:" + progress);
        if (nonNull(status)) {
            fields.add("score:" + score);
        }
        if (nonNull(status)) {
            fields.add("repeat:" + repeat);
        }

        return "SaveMediaListEntry(%s){id}".formatted(join(",", fields));
    }
}
