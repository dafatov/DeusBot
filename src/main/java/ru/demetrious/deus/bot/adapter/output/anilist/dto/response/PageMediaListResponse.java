package ru.demetrious.deus.bot.adapter.output.anilist.dto.response;

import java.util.List;
import lombok.Data;
import ru.demetrious.deus.bot.domain.graphql.ResponseSerialize;

@Data
public class PageMediaListResponse implements ResponseSerialize {
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
