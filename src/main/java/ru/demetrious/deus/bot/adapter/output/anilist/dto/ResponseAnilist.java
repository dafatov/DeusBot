package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.Data;

@Data
public class ResponseAnilist {
    private Map<String, Object> data;

    public <T extends ResponseRsAnilist> T get(String key, Class<T> tClass) {
        return new ObjectMapper().convertValue(data.get(key), tClass);
    }
}
