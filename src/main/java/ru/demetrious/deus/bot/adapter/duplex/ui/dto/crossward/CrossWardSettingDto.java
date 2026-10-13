package ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward;

import lombok.Builder;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.SettingDto;

@Builder
public record CrossWardSettingDto(Long packId) implements SettingDto {
}
