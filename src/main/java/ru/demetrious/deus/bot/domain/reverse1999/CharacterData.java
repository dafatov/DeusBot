package ru.demetrious.deus.bot.domain.reverse1999;

import java.awt.Color;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ru.demetrious.deus.bot.domain.Image;

import static java.util.Optional.ofNullable;

@Data
@Accessors(chain = true)
public class CharacterData implements Serializable {
    private static final Map<Integer, Color> RARITY_COLORS = Map.of(
        2, new Color(69, 93, 68),
        3, new Color(84, 96, 127),
        4, new Color(114, 91, 128),
        5, new Color(183, 156, 92),
        6, new Color(205, 117, 32)
    );
    public static final int MAX_PORTRAIT = 6;

    private Integer id;
    private String name;
    private Image nameImage;
    private Image avatar;
    private Integer rarity;
    private Consume consumeData;

    @Data
    @Accessors(chain = true)
    public static class Consume implements Serializable {
        private Map<Integer, Map<Integer, Integer>> insight;
        private Map<Integer, Map<Integer, Integer>> resonance;
    }

    public @NotNull Color getRarityColor() {
        return ofNullable(RARITY_COLORS.get(rarity)).orElseThrow(() -> new IllegalArgumentException("Can't rarity of character [id=%d] be %d".formatted(id, rarity)));
    }
}
