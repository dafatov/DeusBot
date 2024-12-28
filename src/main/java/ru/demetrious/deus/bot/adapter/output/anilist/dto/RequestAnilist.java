package ru.demetrious.deus.bot.adapter.output.anilist.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.demetrious.deus.bot.adapter.output.anilist.config.RequestAnilistSerializer;

@Data
@Accessors(chain = true)
public class RequestAnilist {
    private QueryAnilist query;

    public static RequestAnilist createQueries(Map<String, ? extends ru.demetrious.deus.bot.adapter.output.anilist.dto.QueryAnilist> queries) {
        return new RequestAnilist().setQuery(new QueryAnilist().setQuery(queries));
    }

    public static RequestAnilist createQuery(String f, ru.demetrious.deus.bot.adapter.output.anilist.dto.QueryAnilist queries) {
        return createQueries(Map.of(f, queries));
    }

    public static RequestAnilist createMutations(Map<String, ? extends ru.demetrious.deus.bot.adapter.output.anilist.dto.MutationAnilist> mutations) {
        return new RequestAnilist().setQuery(new QueryAnilist().setMutation(mutations));
    }

    @Data
    @Accessors(chain = true)
    @JsonSerialize(using = RequestAnilistSerializer.class)
    public static class QueryAnilist {
        private Map<String, ? extends ru.demetrious.deus.bot.adapter.output.anilist.dto.QueryAnilist> query;
        private Map<String, ? extends ru.demetrious.deus.bot.adapter.output.anilist.dto.MutationAnilist> mutation;
    }
}
