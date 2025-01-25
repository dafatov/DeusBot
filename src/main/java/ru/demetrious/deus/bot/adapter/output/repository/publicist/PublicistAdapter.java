package ru.demetrious.deus.bot.adapter.output.repository.publicist;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demetrious.deus.bot.app.api.publicist.GetGuildPublicistOutbound;
import ru.demetrious.deus.bot.app.api.publicist.GetPublicistListOutbound;
import ru.demetrious.deus.bot.app.api.publicist.RemoveGuildPublicistOutbound;
import ru.demetrious.deus.bot.app.api.publicist.SetGuildPublicistOutbound;
import ru.demetrious.deus.bot.domain.Publicist;

@Transactional
@RequiredArgsConstructor
@Component
public class PublicistAdapter implements GetPublicistListOutbound, SetGuildPublicistOutbound, RemoveGuildPublicistOutbound, GetGuildPublicistOutbound {
    private final PublicistRepository publicistRepository;

    @Override
    public List<Publicist> getPublisistList() {
        return publicistRepository.findAll();
    }

    @Override
    public void setGuildPublicist(String guildId, String channelId) {
        publicistRepository.save(new Publicist()
            .setGuildId(guildId)
            .setChannelId(channelId));
    }

    @Override
    public void removeGuildPublicist(String guildId) {
        publicistRepository.deleteById(guildId);
    }

    @Override
    public Optional<String> getGuildPublicist(String guildId) {
        return publicistRepository.findById(guildId).map(Publicist::getChannelId);
    }
}
