package com.supportapp.service.impl;

import com.supportapp.domain.Key;
import com.supportapp.repository.KeyRepository;
import com.supportapp.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class KeyServiceImpl implements KeyService {
    @Autowired
    private KeyRepository keyRepository;
    public KeyServiceImpl(){

    }
    @Override
    public String getPassword(Long id) {
        Optional<Key> key = this.keyRepository.findById(id);
        String passwordKey = key.get().getPassword();
        byte[]decodedBytes = Base64.getDecoder().decode(passwordKey);

        String decodedPassword =  new String(decodedBytes);
        return decodedPassword;
    }

    @Override
    public void setPassword(Key key) {
        String encodedPassword = Base64.getEncoder().encodeToString(key.getPassword().getBytes());
        key.setPassword(encodedPassword);
        this.keyRepository.save(key);
    }
}
