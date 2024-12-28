package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import java.util.List;
import lombok.Data;

@Data
public class PageMediaListRsAnilist implements ResponseRsAnilist {
    private List<Media> media;
    private PageInfo pageInfo;

    @Data
    public static class PageInfo {
        private Boolean hasNextPage;
    }

    @Data
    public static class Media {
        private Integer id;
        private Integer idMal;
    }
}
