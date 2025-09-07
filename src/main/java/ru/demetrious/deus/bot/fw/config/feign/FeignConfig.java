package ru.demetrious.deus.bot.fw.config.feign;

import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.okhttp.OkHttpClient;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import ru.demetrious.deus.bot.app.api.user.FindLinkUserOutbound;
import ru.demetrious.deus.bot.domain.LinkUser;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RequiredArgsConstructor
@Configuration
public class FeignConfig {
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final FindLinkUserOutbound findLinkUserOutbound;
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    RequestInterceptor requestInterceptor() {
        return requestTemplate -> ofNullable(getContext())
            .map(SecurityContext::getAuthentication)
            .map(Principal::getName)
            .flatMap(t -> findLinkUserOutbound.findById(new LinkUser.LinkUserKey()
                .setLinkedRegistrationId(requestTemplate.feignTarget().name())
                .setDiscordPrincipalName(t)))
            .map(LinkUser::getLinkedPrincipalName)
            .map(p -> oAuth2AuthorizedClientService.loadAuthorizedClient(requestTemplate.feignTarget().name(), p))
            .map(OAuth2AuthorizedClient.class::cast)
            .map(OAuth2AuthorizedClient::getAccessToken)
            .ifPresent(
                accessToken -> requestTemplate.header(AUTHORIZATION, "%s %s".formatted(accessToken.getTokenType().getValue(), accessToken.getTokenValue())));
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }
}
