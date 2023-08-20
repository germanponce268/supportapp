package com.supportapp.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttempService {
    public static final int ATTEMPS_MAX = 5;
    public static final int ATTEMPS_INCREMENT = 1;
    private Cache loginAttempCache;
    private CacheManager cacheManager;
    public LoginAttempService(){
        this.cacheManager = new ConcurrentMapCacheManager();
        this.loginAttempCache = this.cacheManager.getCache("users");
    }
    public void evictUserFromLoginAttempCache(String username){
        //this.loginAttempCache.invalidate(username);
    }

    public void addUserToLoginAttempCache(String username) throws ExecutionException {
        int attempts = 0;
        attempts = ATTEMPS_INCREMENT + this.loginAttempCache.get(username, Integer.class);
        this.loginAttempCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttemps(String username) throws ExecutionException {
        return loginAttempCache.get(username, Integer.class) >= ATTEMPS_MAX;
    }

    public boolean isUserInCache(String username){
        if(this.cacheManager.getCache(username).getName() instanceof String){
            return true;
        }
        return false;
    }
}
