package com.supportapp.resource;

import com.supportapp.domain.Key;
import com.supportapp.repository.KeyRepository;
import com.supportapp.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/key")
public class KeyResource {
    @Autowired
    private KeyService keyService;

    @PostMapping("/insert")
    public ResponseEntity<Key> insertKey(@RequestBody Key key) throws Exception {
        this.keyService.setPassword(key);
        return ResponseEntity.ok(key);
    }

}
