package ru.demetrious.deus.bot.domain;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Pull {
    public static final int COLLABORATION_POOL_TYPE = 21;

    private String type;
    private Instant time;
    private Integer poolId;
    private Integer poolType;
    private List<Integer> summonIdList;
}
