package ru.demetrious.deus.bot.domain;

import java.util.List;
import ru.demetrious.deus.bot.app.impl.command.Reverse1999CharactersExportCommandUseCase;

public record CharactersExport(int version, List<CharacterExport> characters) {
    public record CharacterExport(String name, Integer id, Integer count) {
    }
}
