package ru.demetrious.deus.bot.domain.reverse1999;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PullsData {
    private List<Pull> pullList = new ArrayList<>();
    private Map<Integer, Integer> characterCorrelationMap = new HashMap<>();
    private Map<Integer, Integer> materialMap = new HashMap<>();
}
