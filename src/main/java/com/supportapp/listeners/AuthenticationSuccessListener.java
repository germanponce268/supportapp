package com.supportapp.listeners;

import com.supportapp.domain.User;
import com.supportapp.service.LoginAttempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

public class AuthenticationSuccessListener{
    private LoginAttempService loginAttempService;

    public AuthenticationSuccessListener(){

    }
    @Autowired
    public AuthenticationSuccessListener(LoginAttempService loginAttempService){
        this.loginAttempService = loginAttempService;
    }
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof User){
            User user = (User) event.getAuthentication().getPrincipal();
            this.loginAttempService.evictUserFromLoginAttempCache(user.getUsername());
        }
    }
}
