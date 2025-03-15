package ru.demetrious.deus.bot.domain.limiter;

import java.util.function.Function;

public interface Limiter<T> {
    default <R, A> Function<R, A> execute(Function<R, A> function) {
        return function;
    }
}
