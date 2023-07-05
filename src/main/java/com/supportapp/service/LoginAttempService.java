package com.supportapp.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttempService {
    public static final int ATTEMPS_MAX = 5;
    public static final int ATTEMPS_INCREMENT = 1;
    private LoadingCache<String, Integer> loginAttempCache;
    public LoginAttempService(){
        super();
        this.loginAttempCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }
    public void evictUserFromLoginAttempCache(String username){
        this.loginAttempCache.invalidate(username);
    }

    public void addUserToLoginAttempCache(String username) throws ExecutionException {
        int attempts = 0;
        attempts = ATTEMPS_INCREMENT + this.loginAttempCache.get(username);
        this.loginAttempCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttemps(String username) throws ExecutionException {
        return loginAttempCache.get(username) >= ATTEMPS_MAX;
    }

}
