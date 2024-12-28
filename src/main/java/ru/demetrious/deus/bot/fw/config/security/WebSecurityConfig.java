package ru.demetrious.deus.bot.fw.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
import static ru.demetrious.deus.bot.fw.config.security.LinkAuthorizationComponent.createUserFromToken;
import static ru.demetrious.deus.bot.fw.config.security.LinkAuthorizationComponent.updateRequest;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final LinkAuthorizationComponent linkAuthorizationComponent;

    @Bean
    OAuth2AuthorizedClientService oAuth2AuthorizedClientService(JdbcOperations jdbcOperations, ClientRegistrationRepository clientRegistrationRepository) {
        return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
    }

    @Bean
    OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        return new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOAuth2UserService(new DefaultOAuth2UserService());
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                .sessionCreationPolicy(STATELESS))
            .oauth2Login(httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer
                .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                    .authorizationRequestRepository(authorizationRequestRepository()))
                .withObjectPostProcessor(new ObjectPostProcessor<OAuth2LoginAuthenticationFilter>() {
                    @Override
                    public <O extends OAuth2LoginAuthenticationFilter> O postProcess(O oAuth2LoginAuthenticationFilter) {
                        oAuth2LoginAuthenticationFilter.setAuthenticationResultConverter(
                            authenticationResult -> linkAuthorizationComponent.convert(authenticationResult));
                        return oAuth2LoginAuthenticationFilter;
                    }
                })
                .successHandler((request, response, authentication) -> {
                    SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler =
                        new SavedRequestAwareAuthenticationSuccessHandler();

                    linkAuthorizationComponent.handleSuccess((OAuth2AuthenticationToken) authentication);
                    savedRequestAwareAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                }))
            .build();
    }

    @Bean
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new CustomHttpSessionOAuth2AuthorizationRequestRepository(new HttpSessionOAuth2AuthorizationRequestRepository());
    }

    private record CustomHttpSessionOAuth2AuthorizationRequestRepository(
        HttpSessionOAuth2AuthorizationRequestRepository httpSessionOAuth2AuthorizationRequestRepository)
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
        @Override
        public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
            return httpSessionOAuth2AuthorizationRequestRepository.loadAuthorizationRequest(request);
        }

        @Override
        public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
            OAuth2AuthorizationRequest customOAuth2AuthorizationRequest = updateRequest(authorizationRequest, request);
            httpSessionOAuth2AuthorizationRequestRepository.saveAuthorizationRequest(customOAuth2AuthorizationRequest, request, response);
        }

        @Override
        public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
            return httpSessionOAuth2AuthorizationRequestRepository.removeAuthorizationRequest(request, response);
        }
    }

    private record CustomOAuth2UserService(DefaultOAuth2UserService defaultOAuth2UserService) implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
            if (userRequest.getClientRegistration().getRegistrationId().equals("anilist")) {
                return createUserFromToken(userRequest);
            }

            return defaultOAuth2UserService.loadUser(userRequest);
        }
    }
}
