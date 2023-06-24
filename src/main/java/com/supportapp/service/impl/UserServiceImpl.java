package com.supportapp.service.impl;

import com.supportapp.domain.User;
import com.supportapp.domain.UserPrincipal;
import com.supportapp.enums.Role;
import com.supportapp.exceptions.EmailExistException;
import com.supportapp.exceptions.UsernameExistException;
import com.supportapp.repository.UserRepository;
import com.supportapp.service.UserService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

import static com.supportapp.constant.UserImplConstant.*;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {


    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(USER_NOT_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
        } else {
            user.setLastLogindDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            this.userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);

            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setId(Long.valueOf(generateId()));
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        this.userRepository.save(user);
        LOGGER.info("New user password " + password);

        return user;
    }

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_IMAGE_PATH).toUriString();
    }

    private String encodePassword(String password){
        return this.bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUsername)){
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null){
                throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
            }
            if(userByNewUsername != null && currentUser.getId().equals(userByNewUsername.getId())){
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }
            if(userByNewEmail != null && currentUser.getId().equals(userByNewEmail.getId())){
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return currentUser;
        }else{
            if(userByNewUsername != null){
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }

            if(userByNewEmail != null){
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return null;
        }
    }

    @Override
    public List<User> getusers() {
        return this.userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return this.userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }
}
