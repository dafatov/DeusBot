package ru.demetrious.deus.bot.adapter.output.arting.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseDto<T> {
    private Integer code;
    private T data;
    private String message;
}
