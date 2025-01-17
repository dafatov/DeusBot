package ru.demetrious.deus.bot.utils;

import java.util.LinkedList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;

import static java.lang.Math.floorDiv;
import static java.lang.Math.floorMod;
import static java.lang.String.join;
import static java.util.Collections.reverse;
import static java.util.Objects.isNull;

@UtilityClass
public class SpellUtils {
    public static String spell(long number, String[] wordForms) {
        int[] options = new int[]{2, 0, 1, 1, 1};
        int n100 = floorMod(number, 100);
        int n10 = floorMod(number, 10);
        int index = 2;

        if (n10 < 5 && (n100 < 5 || n100 > 19)) {
            index = options[n10];
        }

        return "%d %s".formatted(number, wordForms[index]);
    }

    public static String prettifySeconds(Long seconds) {
        List<String> result = new LinkedList<>();
        List<Pair<String[], Integer>> words = List.of(
            Pair.of(new String[]{"секунда", "секунды", "секунд"}, 60),
            Pair.of(new String[]{"минута", "минуты", "минут"}, 60),
            Pair.of(new String[]{"час", "часа", "часов"}, 24),
            Pair.of(new String[]{"день", "дня", "дней"}, 7),
            Pair.of(new String[]{"неделя", "недели", "недель"}, 4),
            Pair.of(new String[]{"месяц", "месяца", "месяцев"}, 12),
            Pair.of(new String[]{"год", "года", "лет"}, null)
        );

        for (Pair<String[], Integer> word : words) {
            long number;

            if (isNull(word.getRight())) {
                number = seconds;
            } else {
                number = floorMod(seconds, word.getRight());
                seconds = floorDiv(seconds, word.getRight());
            }

            if (number > 0 || seconds == 0 && result.isEmpty()) {
                result.add(spell(number, word.getLeft()));
            }
        }

        reverse(result);
        return join(" ", result);
    }
}
