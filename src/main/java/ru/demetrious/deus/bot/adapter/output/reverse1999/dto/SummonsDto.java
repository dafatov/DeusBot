package ru.demetrious.deus.bot.adapter.output.reverse1999.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SummonsDto(Data data) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(List<Summon> pageData) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Summon(
            List<Integer> gainIds,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-5") Instant createTime,
            String summonType,
            Integer poolId,
            Integer poolType
        ) {
        }
    }
}
