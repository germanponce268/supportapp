package com.supportapp.service;

import com.supportapp.domain.User;
import com.supportapp.exceptions.EmailExistException;
import com.supportapp.exceptions.UsernameExistException;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException;

    List<User> getusers();
    User findUserByUsername(String username);

    User findUserByEmail(String email);

}
