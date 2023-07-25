package com.supportapp.service;

import com.supportapp.domain.User;
import com.supportapp.exceptions.EmailExistException;
import com.supportapp.exceptions.EmailNotFoundException;
import com.supportapp.exceptions.UsernameExistException;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException, MessagingException;

    List<User> getusers();
    User findUserByUsername(String username);

    User findUserByEmail(String email);
    User addNewUser(String firstName, String lastName, String username, String email,String role, boolean isActive, boolean isNonLocked, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException;

    User updateUser(String currentUSername, String firstName,String lastName,String username,String email,String role,boolean isActive,boolean isNonLocked,MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException;
    void deleteUser(long id);
    void resetPassword(String email) throws EmailNotFoundException, MessagingException;
    User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException;

}
