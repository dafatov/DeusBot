package ru.demetrious.deus.bot.app.impl.game.codenames.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.impl.game.codenames.api.Gamebox;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.GameSession;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Setting;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Slf4j
@RequiredArgsConstructor
@Component
public class GameboxImpl implements Gamebox {
    private final ConcurrentMap<String, GameSession> games = new ConcurrentHashMap<>();

    @Override
    public String createNewGame(Setting setting) {
        String key = secure().nextAlphanumeric(6);
        String userId = findPrincipal().getName();

        games.putIfAbsent(key, new GameSession(key, userId, setting));
        return key;
    }

    @Override
    public void joinGame(String gameId) {
        OAuth2AuthenticatedPrincipal principal = findPrincipal();
        Player player = new Player(principal.getName())
            .setName(principal.getAttribute("global_name"))
            .setAvatar(principal.getAttribute("avatar"));

        log.debug("Joined with id={}", player.getId());
        boolean added = games.get(gameId).getPlayerList().add(player);
        log.debug("playerList={}", games.get(gameId).getPlayerList());

        if (!added) {
            throw new RuntimeException("Player is already in game");
        }
    }

    @Override
    public GameSession getGameSession(String gameId, String userId) {
        GameSession gameSession = games.get(gameId);

        if (isNull(gameSession)) {
            throw new RuntimeException("Game is not exist");
        } else if (gameSession.getPlayerList().stream().noneMatch(p -> p.getId().equals(userId))) {
            throw new RuntimeException("Player is not in game");
        }

        return gameSession;
    }

    @Override
    public Optional<Pair<GameSession, Player>> findByPlayer(String userId) {
        return games.values().stream()
            .filter(g -> g.getPlayerList().stream().anyMatch(p -> p.getId().equals(userId)))
            .findFirst()
            .map(g -> Pair.of(g, g.getPlayerList().stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player is not in game"))));
    }

    @Override
    public void removeGame(String gameId) {
        games.remove(gameId);
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private static OAuth2AuthenticatedPrincipal findPrincipal() {
        return ofNullable(getContext())
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getPrincipal)
            .filter(OAuth2AuthenticatedPrincipal.class::isInstance)
            .map(OAuth2AuthenticatedPrincipal.class::cast)
            .filter(p -> isNotBlank(p.getName()))
            .orElseThrow(() -> new IllegalStateException("Could not find player"));
    }
}
