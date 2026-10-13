package ru.demetrious.deus.bot.domain.game;

import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class GameType {
    public static final String CODE_NAMES = "code_names";
    public static final String CROSS_WARD = "cross_ward";
}
