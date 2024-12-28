package ru.demetrious.deus.bot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImportAnimeContext {
    private Integer userId;
    private Integer changesCount;
    private Integer removedCount;
}
