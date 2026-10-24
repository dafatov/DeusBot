package ru.demetrious.deus.bot.adapter.duplex.ui.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.SettingDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.codenames.CodeNamesSettingDto;
import ru.demetrious.deus.bot.adapter.duplex.ui.dto.crossward.CrossWardSettingDto;
import ru.demetrious.deus.bot.app.impl.game.codenames.domain.CodeNamesSetting;
import ru.demetrious.deus.bot.app.impl.game.common.domain.Setting;
import ru.demetrious.deus.bot.app.impl.game.crossward.domain.CrossWardSetting;

import static org.mapstruct.SubclassExhaustiveStrategy.RUNTIME_EXCEPTION;

@Mapper(subclassExhaustiveStrategy = RUNTIME_EXCEPTION)
public interface SettingMapper {
    @SubclassMapping(target = CodeNamesSetting.class, source = CodeNamesSettingDto.class)
    @SubclassMapping(target = CrossWardSetting.class, source = CrossWardSettingDto.class)
    Setting map(SettingDto settingDto);
}
