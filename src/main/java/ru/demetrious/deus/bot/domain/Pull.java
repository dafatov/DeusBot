package ru.demetrious.deus.bot.domain;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Pull {
    private String type;
    private Instant time;
    private Integer poolId;
    private Integer poolType;
    private List<Integer> summonIdList;
}
