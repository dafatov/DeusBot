package ru.demetrious.deus.bot.domain.game;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode
@Data
@Entity
@Accessors(chain = true)
public class Word {
    @Id
    private String text;

    public static Word of(String text) {
        return new Word().setText(text);
    }
}
