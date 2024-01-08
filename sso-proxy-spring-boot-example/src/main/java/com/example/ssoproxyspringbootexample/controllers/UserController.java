package com.example.ssoproxyspringbootexample.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


@Controller
public class UserController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/userinfo")
    public String user(@AuthenticationPrincipal OAuth2User principal, Model model) {

        if(principal != null) {
            model.addAttribute("userInfo", principal.getAttributes());
        }

        return "userDetails";
    }

    @GetMapping("/access-token")
    public String exchange(OAuth2AuthenticationToken authentication, Model model) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService
                .loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        OAuth2AccessToken oAuth2AccessToken = tokenExchange(authorizedClient);
        model.addAttribute("oauthToken", oAuth2AccessToken.getTokenValue());
        return "token";
    }

    private OAuth2AccessToken tokenExchange(OAuth2AuthorizedClient client) {
        ClientRegistration registration = client.getClientRegistration();
        String accessToken = client.getAccessToken().getTokenValue();

        final String url = registration.getProviderDetails().getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", registration.getClientId());
        body.add("client_secret", registration.getClientSecret());
        body.add("grant_type", "client_credentials");
        body.add("subject_token", accessToken);
        body.add("audience", "sso-test");

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate(
                Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        OAuth2AccessTokenResponse response = restTemplate
                .exchange(url, HttpMethod.POST, entity, OAuth2AccessTokenResponse.class).getBody();
        assert response != null;
        return response.getAccessToken();
    }

}
