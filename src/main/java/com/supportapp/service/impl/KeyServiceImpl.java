package com.supportapp.service.impl;

import com.supportapp.domain.Key;
import com.supportapp.repository.KeyRepository;
import com.supportapp.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Optional;

@Service
public class KeyServiceImpl implements KeyService {

    private final String KEY = "idontknowhowtodo";

    @Autowired
    private KeyRepository keyRepository;
    @Override
    public String getPassword(Long id) throws Exception {
        Optional<Key> key = this.keyRepository.findById(id);
        String passwordKey = key.get().getPassword();
        return decrypt(passwordKey);
    }

    @Override
    public void setPassword(Key key) throws Exception{
        String encrypted = encript(key.getPassword());
        key.setPassword(encrypted);
        this.keyRepository.save(key);
    }




    private String encript(String key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, CertificateException, KeyStoreException, IOException {
        java.security.Key aesKey = new SecretKeySpec(this.KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(key.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);

    }
    private String decrypt(String encrypted) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encrypted.replace("\n", ""));
        java.security.Key aesKey = new SecretKeySpec( this.KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        String decrypted = new String(cipher.doFinal(encryptedBytes));
        return decrypted;
    }
}
