package ru.demetrious.deus.bot.adapter.output.arting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetDto {
    private List<String> output;
    @JsonProperty(value = "remain_balance")
    private Integer remainBalance;
}
