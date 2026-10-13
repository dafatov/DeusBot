package ru.demetrious.deus.bot.adapter.duplex.ui.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActionEventDto<A extends ActionDto> extends EventDto {
    private A action;
    private String issuerId;
}
