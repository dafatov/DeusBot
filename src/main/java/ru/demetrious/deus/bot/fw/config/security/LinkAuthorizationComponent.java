package ru.demetrious.deus.bot.fw.config.security;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.user.FindLinkUserOutbound;
import ru.demetrious.deus.bot.app.api.user.SaveLinkUserOutbound;
import ru.demetrious.deus.bot.domain.LinkUser;

import static java.util.Base64.getDecoder;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.emptyCollection;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest.from;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.MAIN_REGISTRATION_ID;
import static ru.demetrious.deus.bot.fw.config.security.AuthorizationComponent.QUERY_DISCORD_USER_ID;
import static ru.demetrious.deus.bot.utils.JacksonUtils.getMapper;

@RequiredArgsConstructor
@Component
public class LinkAuthorizationComponent {
    private static final String REQUEST_DISCORD_USER_ID = "discordUserId";

    private final SaveLinkUserOutbound saveLinkUserOutbound;
    private final FindLinkUserOutbound findLinkUserOutbound;

    protected static OAuth2AuthorizationRequest updateRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        return ofNullable(request)
            .map(r -> r.getParameter(QUERY_DISCORD_USER_ID))
            .map(discordUserId -> {
                Map<String, Object> additionalParameters = new HashMap<>(authorizationRequest.getAdditionalParameters());

                additionalParameters.put(REQUEST_DISCORD_USER_ID, discordUserId);
                return from(authorizationRequest)
                    .additionalParameters(additionalParameters)
                    .build();
            })
            .orElseThrow(() -> new IllegalArgumentException("Main registration user id can't be null"));
    }

    protected static @NotNull DefaultOAuth2User createUserFromToken(@NotNull OAuth2UserRequest userRequest) {
        try {
            String nameAttributeKey = "id";
            String payload = userRequest.getAccessToken().getTokenValue().split("\\.")[1];
            Object sub = getMapper().readValue(getDecoder().decode(payload), Map.class).get("sub");

            return new DefaultOAuth2User(emptyCollection(), Map.of(nameAttributeKey, sub), nameAttributeKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected @NotNull CustomOAuth2AuthenticationToken convert(@NotNull OAuth2LoginAuthenticationToken authenticationResult) {
        CustomOAuth2AuthenticationToken oAuth2AuthenticationToken =
            new CustomOAuth2AuthenticationToken(authenticationResult.getPrincipal(), authenticationResult.getAuthorities(),
                authenticationResult.getClientRegistration().getRegistrationId());

        ofNullable(authenticationResult.getAuthorizationExchange())
            .map(OAuth2AuthorizationExchange::getAuthorizationRequest)
            .map(OAuth2AuthorizationRequest::getAdditionalParameters)
            .map(additionalParameters -> additionalParameters.get(REQUEST_DISCORD_USER_ID))
            .ifPresent(oAuth2AuthenticationToken::setCredentials);
        return oAuth2AuthenticationToken;
    }

    protected void handleSuccess(OAuth2AuthenticationToken authentication) {
        requireNonNull(authentication.getCredentials(), "Main registration user id can't be null");
        LinkUser.LinkUserKey linkUserKey = new LinkUser.LinkUserKey()
            .setDiscordPrincipalName(String.valueOf(authentication.getCredentials()))
            .setLinkedRegistrationId(authentication.getAuthorizedClientRegistrationId());

        if (!equalsIgnoreCase(MAIN_REGISTRATION_ID, linkUserKey.getLinkedRegistrationId())) {
            saveLinkUserOutbound.save(new LinkUser()
                .setLinkUserKey(linkUserKey)
                .setLinkedPrincipalName(authentication.getPrincipal().getName()));
        }
    }

    protected Optional<String> getLinkedPrincipalName(String shikimoriRegistrationId, String userId) {
        return findLinkUserOutbound.findById(new LinkUser.LinkUserKey()
                .setLinkedRegistrationId(shikimoriRegistrationId)
                .setDiscordPrincipalName(userId))
            .map(LinkUser::getLinkedPrincipalName);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private static class CustomOAuth2AuthenticationToken extends OAuth2AuthenticationToken {
        @Setter
        private Object credentials;

        public CustomOAuth2AuthenticationToken(OAuth2User principal, Collection<? extends GrantedAuthority> authorities,
                                               String authorizedClientRegistrationId) {
            super(principal, authorities, authorizedClientRegistrationId);
        }

        @Override
        public Object getCredentials() {
            return credentials;
        }
    }
}
