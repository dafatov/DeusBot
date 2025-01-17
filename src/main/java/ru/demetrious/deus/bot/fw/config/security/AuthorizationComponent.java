package ru.demetrious.deus.bot.fw.config.security;

import java.net.URISyntaxException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.emptyCollection;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.security.oauth2.client.OAuth2AuthorizeRequest.withClientRegistrationId;
import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

@RequiredArgsConstructor
@Component
public class AuthorizationComponent {
    public static final String MAIN_REGISTRATION_ID = "discord";
    public static final String ANILIST_REGISTRATION_ID = "anilist";
    public static final String SHIKIMORI_REGISTRATION_ID = "shikimori";
    protected static final String QUERY_DISCORD_USER_ID = "id";

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
    private final LinkAuthorizationComponent linkAuthorizationComponent;

    @Value("${app.url}")
    private String appUrl;

    public Optional<OAuth2AuthorizedClient> authorize(String registrationId, String userId) {
        Optional<OAuth2AuthorizedClient> oAuth2AuthorizedClientOptional;

        try {
            oAuth2AuthorizedClientOptional = ofNullable(userId)
                .filter(u -> MAIN_REGISTRATION_ID.equals(registrationId))
                .or(() -> linkAuthorizationComponent.getLinkedPrincipalName(registrationId, userId))
                .map(principalName -> oAuth2AuthorizedClientManager.authorize(withClientRegistrationId(registrationId)
                    .principal(principalName)
                    .build()));
        } catch (ClientAuthorizationException ex) {
            oAuth2AuthorizedClientOptional = empty();
        }

        oAuth2AuthorizedClientOptional.filter(o -> MAIN_REGISTRATION_ID.equals(o.getClientRegistration().getRegistrationId()))
            .ifPresent(oAuth2AuthorizedClient -> getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                oAuth2AuthorizedClient.getPrincipalName(),
                oAuth2AuthorizedClient,
                emptyCollection()
            )));

        return oAuth2AuthorizedClientOptional;
    }

    public @NotNull String getUrl(String userId, String registrationId) {
        try {
            return new URIBuilder(appUrl)
                .setPath("%s/%s".formatted(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI, registrationId))
                .addParameter(QUERY_DISCORD_USER_ID, userId)
                .build()
                .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
