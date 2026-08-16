package ru.demetrious.deus.bot.app.impl.game.common;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import ru.demetrious.deus.bot.app.api.game.NotifyGameStateOutbound;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Action;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionContext;
import ru.demetrious.deus.bot.app.impl.game.common.domain.ActionException;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Event;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Instance;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Player;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static ru.demetrious.deus.bot.utils.JacksonUtils.writeValueAsString;

@Slf4j
public abstract class Processor<G extends Instance<S, P, A>, S extends Setting, P extends Player, C extends ActionContext<G>, A extends Action<P, G, C>> {
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

    public abstract P createNewPlayer(String id, String name, String avatar);

    public String createNewGame(Setting setting) {
        String key = secure().nextAlphanumeric(6);
        String userId = findPrincipal().getName();

        games.putIfAbsent(key, createNewGame(key, userId, settingClass.cast(setting)));
        return key;
    }

    public void joinGame(String key) {
        G game = games.get(key);
        OAuth2AuthenticatedPrincipal principal = findPrincipal();
        P newPlayer = createNewPlayer(principal.getName(), principal.getAttribute("global_name"), principal.getAttribute("avatar"));

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

    public Optional<Pair<G, P>> findPlayer(String userId) {
        return games.values().stream()
            .flatMap(g -> g.getPlayerList().stream()
                .filter(p -> p.getId().equals(userId))
                .map(p -> Pair.of(g, p)))
            .findFirst();
    }

    public void performAction(String key, String userId, Action<?, ?, ?> rawAction) throws ActionException {
        G game = games.get(key);
        A action = actionClass.cast(rawAction);
        P player = game.getPlayerList().stream()
            .filter(p -> p.getId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new ActionException("Player is not in game"));

        action.perform(game, player, context);
        game.getHistory().addFirst(new Event<>(action, player));
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
