package ru.demetrious.deus.bot.domain.reverse1999;

import java.util.List;

public record CharactersExport(int version, List<CharacterExport> characters) {
    public record CharacterExport(String name, Integer id, Integer count) {
    }
}
