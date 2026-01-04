package ru.demetrious.deus.bot.domain.reverse1999;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.domain.Image;

@Data
@Accessors(chain = true)
public class ItemData implements Serializable {
    private int order;
    @JsonIgnore
    private Image image;
    private Map<Integer, Integer> craft = Map.of();
}
