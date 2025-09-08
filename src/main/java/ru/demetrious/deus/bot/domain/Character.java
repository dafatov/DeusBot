package ru.demetrious.deus.bot.domain;

import java.awt.image.BufferedImage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Character {
    public static final int CHARACTERS_MAX_PORTRAIT = 6;

    private Integer id;
    private String name;
    private BufferedImage nameImage;
    private BufferedImage avatar;
    private Integer rarity;
}
