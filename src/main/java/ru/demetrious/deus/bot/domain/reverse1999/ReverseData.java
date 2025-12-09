package ru.demetrious.deus.bot.domain.reverse1999;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReverseData implements Serializable {
    private Map<Integer, CharacterData> characters;
    private Map<Integer, ItemData> items;
    private Map<Integer, LevelData> levels;
}
