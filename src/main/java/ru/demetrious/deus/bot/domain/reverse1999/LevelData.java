package ru.demetrious.deus.bot.domain.reverse1999;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LevelData implements Serializable {
    private String name;
    private Integer cost;
    private Map<Integer, Drop> dropMap = new HashMap<>();

    @Data
    @Accessors(chain = true)
    public static class Drop implements Serializable {
        private Probability probability;
        private Double mathematicalExpectation;

        public enum Probability {
            COMMON, FIXED, POSSIBLE, RARE
        }
    }
}
