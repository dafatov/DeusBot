package ru.demetrious.deus.bot.app.impl.aniguessr;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.domain.Franchise;

import static java.lang.Integer.compare;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder.Status.ADDED;
import static ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder.Status.DUPLICATE;
import static ru.demetrious.deus.bot.app.impl.aniguessr.AniguessrGamesHolder.Status.GUESSED;

@Component
public class AniguessrGamesHolder {
    private static final Map<UUID, Game> GAMES = new ConcurrentHashMap<>();
    private static final String EQUAL_EMOJI = ":white_check_mark:";
    private static final String NOT_EQUAL_EMOJI = ":no_entry:";
    private static final String LESS_EMOJI = ":arrow_up:";
    private static final String MORE_EMOJI = ":arrow_down:";

    public void create(UUID id, String threadId, Franchise franchise) {
        GAMES.put(id, new Game(threadId, franchise));
    }

    public Status guess(UUID key, Franchise franchise) {
        Game franchiseListPair = GAMES.get(key);

        if (!franchiseListPair.getGuesses().add(franchise)) {
            return DUPLICATE;
        }

        if (franchiseListPair.getAnswer().equals(franchise)) {
            return GUESSED;
        }

        return ADDED;
    }

    public Set<UUID> getGames() {
        return GAMES.keySet();
    }

    public String getLastGuess(UUID id) {
        Game game = GAMES.get(id);

        return game.getGuesses().reversed().stream()
            .findFirst()
            .map(guess -> mapGuess(game.getAnswer(), guess))
            .orElseThrow();
    }

    public String getThreadId(UUID id) {
        return GAMES.get(id).getThreadId();
    }

    public int getGuessesCount(UUID id) {
        return GAMES.get(id).getGuesses().size();
    }

    public String remove(UUID id) {
        Franchise answer = GAMES.remove(id).getAnswer();

        return mapGuess(answer, answer);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private String mapGuess(Franchise answer, Franchise guess) {
        return "### - %s\n-# - %s\n-# - %s\n-# - %s\n-# - %s\n-# - %s\n-# - %s".formatted(
            defaultIfBlank(guess.getFirstTitle(), guess.getName()),
            defaultIfBlank(w(compare(answer.getMinAiredOnYear(), guess.getMinAiredOnYear()), valueOf(guess.getMinAiredOnYear())), "-"),
            defaultIfBlank(guess.getThemes().stream().map(f -> w(answer.getThemes().contains(f), f)).collect(joining(", ")), "-"),
            defaultIfBlank(guess.getGenres().stream().map(f -> w(answer.getGenres().contains(f), f)).collect(joining(", ")), "-"),
            defaultIfBlank(guess.getStudios().stream().map(f -> w(answer.getStudios().contains(f), f)).collect(joining(", ")), "-"),
            defaultIfBlank(guess.getSources().stream().map(f -> w(answer.getSources().contains(f), f.getLocalized())).collect(joining(", ")), "-"),
            defaultIfBlank(w(Double.compare(answer.getAverageScore(), guess.getAverageScore()), format("%.2f", guess.getAverageScore())), "-")
        );
    }

    private String w(boolean b, String s) {
        return "%s %s".formatted(b ? EQUAL_EMOJI : NOT_EQUAL_EMOJI, s);
    }

    private String w(int i, String s) {
        String result = LESS_EMOJI;

        if (i == 0) {
            result = EQUAL_EMOJI;
        } else if (i < 0) {
            result = MORE_EMOJI;
        }

        return "%s %s".formatted(result, s);
    }

    public enum Status {
        GUESSED, DUPLICATE, ADDED
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(chain = true)
    private static class Game {
        private final String threadId;
        private final Franchise answer;
        private final SequencedSet<Franchise> guesses = new LinkedHashSet<>();
    }
}
