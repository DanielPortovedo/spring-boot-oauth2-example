package com.example.ssoproxyspringbootexample.controllers;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(OAuth2AuthenticationToken oAuth2AuthenticationToken, Model model) {
        model.addAttribute("authenticated", oAuth2AuthenticationToken != null && oAuth2AuthenticationToken.isAuthenticated());


        return "index";
    }


    @GetMapping("/secure")
    public String secure() {
        return "secure";
    }

}
