package com.example.ssoproxyspringbootexample.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //public static final String LOGOUT_REDIRECT_URI = "https://k8s-daniel-tomcat-sample.cern.ch/";

    public static final String LOGOUT_REDIRECT_URI = "http://localhost:8080/";
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(
                        this.clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri(LOGOUT_REDIRECT_URI);
        return oidcLogoutSuccessHandler;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests( auth -> {
                    auth.requestMatchers(antMatcher("/")).permitAll();
                    auth.anyRequest().authenticated();
                })
                .logout(l -> l.logoutSuccessHandler(oidcLogoutSuccessHandler()))
                .oauth2Login(withDefaults())
                //.formLogin(withDefaults()) //use default form login instead of only oauth2
                .build();
    }
}

