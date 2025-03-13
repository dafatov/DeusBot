package ru.demetrious.deus.bot.adapter.output.anilist.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.enums.MediaListStatusAnilist;
import ru.demetrious.deus.bot.domain.graphql.ResponseSerialize;

@Data
public class MediaListCollectionResponse implements ResponseSerialize {
    private List<Lists> lists;

    @Data
    public static class Lists {
        private List<Entries> entries;

        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode
        @Data
        public static class Entries {
            @EqualsAndHashCode.Exclude
            private Integer id;
            private Media media;
            private Integer progress;
            private Integer repeat;
            private Double score;
            private MediaListStatusAnilist status;

            @NoArgsConstructor
            @EqualsAndHashCode
            @AllArgsConstructor
            @Data
            public static class Media {
                private Integer id;
                private Integer idMal;
                @EqualsAndHashCode.Exclude
                private Integer episodes;
            }
        }
    }
}
