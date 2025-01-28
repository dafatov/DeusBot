package ru.demetrious.deus.bot.app.impl.player.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static ru.demetrious.deus.bot.app.impl.player.domain.Result.Status.OK;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class Result<T> {
    private final Status status;
    private final T data;

    public static <T> Result<T> of(T data) {
        return new Result<>(OK, data);
    }

    public static <T> Result<T> of(Status status) {
        return new Result<>(status, null);
    }

    public enum Status {
        IS_NOT_PLAYING, IS_PLAYING_LIVE, NOT_SAME_CHANNEL, OK, UNBOUND
    }
}
