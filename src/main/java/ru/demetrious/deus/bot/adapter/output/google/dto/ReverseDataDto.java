package ru.demetrious.deus.bot.adapter.output.google.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseDataDto {
    private List<PullDto> pullList;
    private Map<Integer, Integer> characterCorrelationMap = new HashMap<>();
}
