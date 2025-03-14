package ru.demetrious.deus.bot.domain.graphql;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.fw.serializer.RequestSerializer;

@Data
@Accessors(chain = true)
public class Request {
    private RequestInner query;

    public static Request createQueries(Map<String, ? extends Query> queries) {
        return new Request().setQuery(new RequestInner().setQuery(queries));
    }

    public static Request createQuery(String f, Query queries) {
        return createQueries(Map.of(f, queries));
    }

    public static Request createMutations(Map<String, ? extends Mutation> mutations) {
        return new Request().setQuery(new RequestInner().setMutation(mutations));
    }

    @Data
    @Accessors(chain = true)
    @JsonSerialize(using = RequestSerializer.class)
    public static class RequestInner {
        private Map<String, ? extends Query> query;
        private Map<String, ? extends Mutation> mutation;
    }
}
