package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import java.util.Map;
import lombok.Data;

import static ru.demetrious.deus.bot.utils.JacksonUtils.getMapper;

@Data
public class ResponseAnilist {
    private Map<String, Object> data;

    public <T extends ResponseRsAnilist> T get(String key, Class<T> tClass) {
        return getMapper().convertValue(data.get(key), tClass);
    }
}
