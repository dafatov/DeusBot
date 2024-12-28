package ru.demetrious.deus.bot;

import org.junit.jupiter.api.Test;
import ru.demetrious.deus.bot.adapter.output.anilist.dto.SaveMediaListEntryAnilist;

public class SerializeTest {
    @Test
    void test1() {
        System.out.println(new SaveMediaListEntryAnilist(
            null,
            2354,
            444,
            8.0,
            0
        ).serialize());
    }
}
