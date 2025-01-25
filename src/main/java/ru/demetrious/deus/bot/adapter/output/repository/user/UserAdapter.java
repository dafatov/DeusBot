package ru.demetrious.deus.bot.adapter.output.repository.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demetrious.deus.bot.app.api.user.FindLinkUserOutbound;
import ru.demetrious.deus.bot.app.api.user.SaveLinkUserOutbound;
import ru.demetrious.deus.bot.domain.LinkUser;

@Transactional
@RequiredArgsConstructor
@Component
public class UserAdapter implements SaveLinkUserOutbound, FindLinkUserOutbound {
    private final LinkUserRepository linkUserRepository;

    @Override
    public void save(LinkUser linkUser) {
        linkUserRepository.save(linkUser);
    }

    @Override
    public Optional<LinkUser> findById(LinkUser.LinkUserKey linkUserKey) {
        return linkUserRepository.findById(linkUserKey);
    }
}
