package ru.demetrious.deus.bot.app.impl.game.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionContext;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionEvent;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static ru.demetrious.deus.bot.utils.JacksonUtils.writeValueAsString;

@Slf4j
public abstract class Processor<G extends Instance<S, P>, S extends Setting, P extends Player, C extends ActionContext<G>, A extends Action<P, G, C, A>> {
    private final ConcurrentMap<String, G> games = new ConcurrentHashMap<>();
    private final NotifyGameStateOutbound notifyGameStateOutbound;
    private final Class<A> actionClass;
    private final Class<S> settingClass;
    private final C context;

    protected Processor(NotifyGameStateOutbound notifyGameStateOutbound, Class<A> actionClass, Class<S> settingClass, C context) {
        this.notifyGameStateOutbound = notifyGameStateOutbound;
        this.actionClass = actionClass;
        this.settingClass = settingClass;
        this.context = context;
    }

    public abstract String getGame();

    public abstract G createNewGame(String key, String hostId, S setting);

    public abstract P createNewPlayer(G game, String id, String name, String avatar);

    public void onPlayerDisconnect(G game, P player) throws ActionException {
        if (game.getHostId().equals(player.getId())) {
            game.setHostId(game.getPlayerList().getFirst().getId());
        }
    }

    public String createNewGame(Setting setting) {
        String key = secure().nextAlphanumeric(6);
        String userId = findPrincipal().getName();

        games.putIfAbsent(key, createNewGame(key, userId, settingClass.cast(setting)));
        return key;
    }

    public void joinGame(String key) {
        G game = games.get(key);
        OAuth2AuthenticatedPrincipal principal = findPrincipal();
        P newPlayer = createNewPlayer(game, principal.getName(), principal.getAttribute("global_name"), principal.getAttribute("avatar"));

        log.debug("Joined with id={}", newPlayer.getId());
        boolean added = game.getPlayerList().add(newPlayer);
        log.trace("playerList={}", writeValueAsString(game.getPlayerList()));

        //noinspection ConstantValue
        if (!added) {
            throw new RuntimeException("Player is already in game");
        }
    }

    public boolean containsGame(String key) {
        return games.containsKey(key);
    }

    public void removeGame(String key) {
        games.remove(key);
    }

    public void connect(String userId) {
        games.values().forEach(game -> game.getPlayerList().stream()
            .filter(p -> userId.equals(p.getId()))
            .findFirst()
            .ifPresent(player -> {
                if (!player.isDisconnected()) {
                    return;
                }

                log.debug("Cancel Disconnect Timer for {}", player.getId());
                player.getDisconnectCompletableFuture().cancel(true);
                player.setDisconnectCompletableFuture(null);
                notifyGameStateOutbound.notifyGameState(game);
            }));
    }

    public void disconnect(String userId) {
        games.values().forEach(game -> game.getPlayerList().stream()
            .filter(p -> userId.equals(p.getId()))
            .findFirst()
            .ifPresent(player -> {
                player.setDisconnectCompletableFuture(runAsync(() -> {
                    game.removePlayer(player);

                    log.debug("Disconnect for {}", player.getId());
                    if (game.getPlayerList().isEmpty()) {
                        log.debug("Remove game cause no players: {}", game.getKey());
                        removeGame(game.getKey());
                        return;
                    }

                    try {
                        onPlayerDisconnect(game, player);
                    } catch (ActionException e) {
                        log.error("onPlayerDisconnect failed while disconnected for {}", player.getId(), e);
                    }
                    notifyGameStateOutbound.notifyGameState(game);
                }, delayedExecutor(30, SECONDS, context.getVirtualThreadPerTaskExecutor())));
                notifyGameStateOutbound.notifyGameState(game);
            }));
    }

    public void performAction(String key, String userId, Action<?, ?, ?, ?> rawAction) throws ActionException {
        G game = games.get(key);
        A action = actionClass.cast(rawAction);
        P player = game.getPlayerList().stream()
            .filter(p -> p.getId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new ActionException("Player is not in game"));

        action.perform(game, player, context, game.addEvent(id -> new ActionEvent<>(id, action, player)));
        notifyGameStateOutbound.notifyGameState(game);
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
