package ru.demetrious.deus.bot.adapter.output.arting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateDto {
    @JsonProperty(value = "request_id")
    private String requestId;
}
