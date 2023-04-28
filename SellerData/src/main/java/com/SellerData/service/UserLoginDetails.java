package com.SellerData.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserLoginDetails {

    @Autowired
    private UserDetailsService userDetailsService;

    public String details() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String login = authentication.getName();
        System.out.println("login user>=="+login);


        return login;
    }
}
