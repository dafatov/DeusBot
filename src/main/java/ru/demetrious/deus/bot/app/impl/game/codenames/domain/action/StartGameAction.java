package ru.demetrious.deus.bot.app.impl.game.codenames.domain.action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Builder;
import org.apache.commons.lang3.tuple.Pair;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesActionContext;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesInstance;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Word;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.Word.Color;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Streams.zip;
import static java.time.Duration.ofMinutes;
import static java.util.Collections.nCopies;
import static java.util.Collections.shuffle;
import static java.util.Collections.singleton;
import static java.util.concurrent.ThreadLocalRandom.current;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkHost;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.checkLocked;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesAction.endHintingPhaseTimeout;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesPlayer.Team;
import static ru.demetrious.deus.bot.app.impl.game.codenames.domain.instance.State.Phase.HINTING;

@Builder
public record StartGameAction() implements CodeNamesAction {
    @Override
    public void perform(CodeNamesInstance gameSession, CodeNamesPlayer player, CodeNamesActionContext ctx, ActionEvent<CodeNamesAction, CodeNamesPlayer> event) throws ActionException {
        checkLocked(gameSession);
        checkHost(gameSession, player);

        Pair<Color, List<Word>> wordList = createWordList(gameSession.getSetting().packId(), ctx);
        gameSession.getWordList().clear();
        gameSession.getWordList().addAll(wordList.getRight());

        gameSession.getState().setTeam(wordList.getLeft() == Color.RED ? Team.RED : Team.BLUE);
        gameSession.getVoteMap().clear();
        gameSession.getHintList().clear();
        gameSession.getState().setPhase(HINTING);
        gameSession.getState().setRound(1);
        gameSession.getState().setLocked(true);
        gameSession.getState().getScore().clear();
        gameSession.getState().getScore().add(wordList.getLeft() == Color.RED ? Team.RED : Team.BLUE, 9);
        gameSession.getState().getScore().add(wordList.getLeft() != Color.RED ? Team.RED : Team.BLUE, 8);
        ctx.startTimer(gameSession, ofMinutes(2), () -> endHintingPhaseTimeout(gameSession, ctx));
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private Pair<Color, List<Word>> createWordList(Long packId, CodeNamesActionContext ctx) {
        List<String> wordList = new ArrayList<>(ctx.getDictionary().getWords(packId));
        ThreadLocalRandom random = current();
        Color color = random.nextBoolean() ? Color.RED : Color.BLUE;
        List<Color> colors = newArrayList(concat(
            nCopies(9, color),
            nCopies(8, color == Color.BLUE ? Color.RED : Color.BLUE),
            nCopies(7, Color.WHITE),
            singleton(Color.BLACK)
        ));

        shuffle(colors, random);
        shuffle(wordList, random);
        //noinspection UnstableApiUsage
        return Pair.of(color, zip(wordList.stream(), colors.stream(), Word::new)
            .limit(colors.size())
            .toList());
    }
}
