package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import org.mapstruct.Mapper;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.SettingDto;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.Setting;

@Mapper
public interface SettingMapper {
    Setting map(SettingDto settingDto);
}
