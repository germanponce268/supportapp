package com.supportapp.service;

import com.supportapp.domain.Key;

public interface KeyService {

    public String getPassword(Long id);

    public void setPassword(Key key);
}
