package ru.demetrious.deus.bot.fw.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.user.SaveLinkUserOutbound;
import ru.demetrious.deus.bot.domain.LinkUser;

import static java.util.Base64.getDecoder;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.emptyCollection;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest.from;

@RequiredArgsConstructor
@Component
public class LinkAuthorizationComponent {
    private static final String REQUEST_DISCORD_USER_ID = "discordUserId";
    private static final String QUERY_DISCORD_USER_ID = "id";
    private static final String MAIN_REGISTRATION_ID = "discord";

    private final SaveLinkUserOutbound saveLinkUserOutbound;

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
            .orElse(authorizationRequest);
    }

    protected static @NotNull DefaultOAuth2User createUserFromToken(@NotNull OAuth2UserRequest userRequest) {
        try {
            String nameAttributeKey = "id";
            String payload = userRequest.getAccessToken().getTokenValue().split("\\.")[1];
            Object sub = new ObjectMapper().readValue(getDecoder().decode(payload), Map.class).get("sub");

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
        LinkUser.LinkUserKey linkUserKey = new LinkUser.LinkUserKey()
            .setDiscordPrincipalName(String.valueOf(authentication.getCredentials()))
            .setLinkedRegistrationId(authentication.getAuthorizedClientRegistrationId());

        if (!equalsIgnoreCase(MAIN_REGISTRATION_ID, linkUserKey.getLinkedRegistrationId())) {
            saveLinkUserOutbound.save(new LinkUser()
                .setLinkUserKey(linkUserKey)
                .setLinkedPrincipalName(authentication.getPrincipal().getName()));
        }
    }

    public @NotNull String getUrl(String userId, String registrationId) {
        try {
            //TODO как-то сделать для стендов урлы (скорее всего через env)
            return new URIBuilder("http://localhost:8080")
                .setPath("/oauth2/authorization/%s".formatted(registrationId))
                .addParameter(QUERY_DISCORD_USER_ID, userId)
                .build()
                .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void authenticate(OAuth2AuthorizedClient authorizedClient) {
        getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
            authorizedClient.getPrincipalName(),
            authorizedClient,
            emptyCollection()
        ));
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
