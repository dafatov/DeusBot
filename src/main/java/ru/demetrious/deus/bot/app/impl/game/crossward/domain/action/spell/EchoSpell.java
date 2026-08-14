package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action.spell;

import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Spell;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Word;

import static java.util.Map.Entry;
import static java.util.Map.of;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag.ECHO_CONSONANT;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag.ECHO_VOWEL;

@Slf4j
@Builder
public record EchoSpell(int wordId) implements Spell {
    private static final Map<Tag, Set<Character>> TAG_ECHO = of(
        ECHO_CONSONANT, Set.of('б', 'в', 'г', 'д', 'ж', 'з', 'й', 'к', 'л', 'м', 'н', 'п', 'р', 'с', 'т', 'ф', 'х', 'ц', 'ч', 'ш', 'щ'),
        ECHO_VOWEL, Set.of('а', 'о', 'и', 'е', 'ё', 'э', 'ы', 'у', 'ю', 'я')
    );

    @Override
    public void use(CrossWardInstance gameSession, String userId, CrossWardActionContext ctx) throws ActionException {
        Word word = gameSession.getWords().stream()
            .filter(w -> w.getOrder() == wordId)
            .findFirst()
            .orElseThrow(() -> new ActionException("Word not found"));

        word.getCells().forEach(cell -> TAG_ECHO.entrySet().stream()
            .filter(entry -> entry.getValue().contains(cell.getLetter()))
            .findFirst()
            .map(Entry::getKey)
            .ifPresent(t -> cell.getTags().add(t)));
    }
}
