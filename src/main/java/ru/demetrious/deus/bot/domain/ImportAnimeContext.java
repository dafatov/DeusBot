package ru.demetrious.deus.bot.domain;

import java.net.URI;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImportAnimeContext {
    private List<AnimeProjection> added;
    private List<AnimeProjection> edited;
    private List<AnimeProjection> removed;
    private List<AnimeProjection> skipped;
    private List<AnimeProjection> another;

    @Data
    @Accessors
    public static class AnimeProjection {
        private String title;
        private URI url;
    }
}
