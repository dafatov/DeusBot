package ru.demetrious.deus.bot.domain.graphql;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;

import static ru.demetrious.deus.bot.utils.JacksonUtils.getMapper;

@Data
public class Response {
    private Map<String, ? super ResponseSerialize> data;

    public <T extends ResponseSerialize> T get(String key, Class<T> tClass) {
        return getMapper().convertValue(data.get(key), tClass);
    }

    public <T extends ResponseSerialize> List<T> getList(String key, Class<T> tClass) {
        return getMapper().convertValue(data.get(key), getMapper().getTypeFactory().constructCollectionType(List.class, tClass));
    }

    public <T extends ResponseSerialize> List<List<T>> getPowerList(Class<T> tClass) {
        return data.keySet().stream()
            .map(key -> getList(key, tClass))
            .collect(Collectors.toList());
    }
}
