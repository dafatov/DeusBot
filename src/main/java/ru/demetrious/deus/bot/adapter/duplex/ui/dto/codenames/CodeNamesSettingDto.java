package ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames;

import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.SettingDto;

@Builder
public record CodeNamesSettingDto(Long packId) implements SettingDto {
}
