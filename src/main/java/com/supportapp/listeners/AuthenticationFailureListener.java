package com.supportapp.listeners;

import com.supportapp.service.LoginAttempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
@Component
public class AuthenticationFailureListener {
    private LoginAttempService loginAttempService;

    @Autowired
    public AuthenticationFailureListener(LoginAttempService loginAttempService){
        this.loginAttempService = loginAttempService;
    }
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) throws ExecutionException {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof  String){
            String username = (String) event.getAuthentication().getPrincipal();
            this.loginAttempService.addUserToLoginAttempCache(username);
        }
    }
}
