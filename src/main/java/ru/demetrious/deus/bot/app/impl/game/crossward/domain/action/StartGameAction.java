package ru.demetrious.deus.bot.app.impl.game.crossward.domain.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardActionContext;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardInstance;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardPlayer;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag;

import static java.time.Duration.ofMinutes;
import static java.util.Collections.shuffle;
import static java.util.Comparator.comparingDouble;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.function.Failable.asRunnable;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.checkPlayers;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardAction.endPlayerPhase;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.State.Phase.PLAYING;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag.FREQUENCY_HIGH;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag.FREQUENCY_LOW;
import static ru.demetrious.deus.bot.app.impl.game.crossward.domain.instance.Tag.FREQUENCY_MEDIUM;
import static ru.demetrious.deus.bot.app.impl.game.crossward.utils.CrosswordUtils.placeStartWords;
import static ru.demetrious.deus.bot.app.impl.game.crossward.utils.CrosswordUtils.placeWord;

@Slf4j
@Builder
public record StartGameAction() implements CrossWardAction {
    @Override
    public void perform(CrossWardInstance gameSession, CrossWardPlayer player, CrossWardActionContext ctx) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, player);
        checkPlayers(gameSession);

        startBoard(gameSession, ctx);
        gameSession.getState().setPhase(PLAYING);
        gameSession.getState().setLocked(true);
        gameSession.getState().setCurrentPlayer(gameSession.getPlayerList().getFirst());
        gameSession.getPlayerList().forEach(p -> p.setScore(0));
        placeWord(gameSession, gameSession.getState().getCurrentPlayer());
        ctx.startTimer(gameSession, ofMinutes(2), asRunnable(() -> endPlayerPhase(gameSession, ctx)));
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private static void startBoard(CrossWardInstance gameSession, CrossWardActionContext ctx) {
        List<String> words = new ArrayList<>(ctx.getDictionary().getWords(gameSession.getSetting().packId()));

        shuffle(words, current());
        gameSession.getAvailableWords().clear();
        gameSession.getAvailableWords().addAll(words);
        calcFrequencies(gameSession, words);
        gameSession.getWords().clear();
        gameSession.getGrid().clear();

        placeStartWords(gameSession);
    }

    private static void calcFrequencies(CrossWardInstance gameSession, List<String> words) {
        List<Tag> tags = List.of(FREQUENCY_LOW, FREQUENCY_MEDIUM, FREQUENCY_HIGH);
        Map<Character, Integer> docFreq = new HashMap<>();
        KMeansPlusPlusClusterer<LetterPoint> clusterer = new KMeansPlusPlusClusterer<>(tags.size());

        words.forEach(word -> word.chars().distinct().forEach(c -> docFreq.merge((char) c, 1, Integer::sum)));

        List<CentroidCluster<LetterPoint>> clusters = clusterer.cluster(docFreq.entrySet().stream()
            .map(e -> new LetterPoint(e.getValue(), e.getKey()))
            .collect(toList()));

        clusters.sort(comparingDouble(c -> c.getCenter().getPoint()[0]));
        for (int i = 0; i < tags.size(); i++) {
            Tag tag = tags.get(i);
            Set<Character> letters = clusters.get(i).getPoints().stream()
                .map(LetterPoint::getLetter)
                .collect(toSet());

            gameSession.getLetterTags().computeIfAbsent(tag, _ -> new HashSet<>()).addAll(letters);
        }
    }

    @Getter
    private static class LetterPoint extends DoublePoint {
        private final Character letter;

        public LetterPoint(int point, Character letter) {
            super(new int[]{point});
            this.letter = letter;
        }
    }
}
