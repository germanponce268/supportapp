package com.supportapp.service.impl;

import com.supportapp.constant.FileConstant;
import com.supportapp.domain.User;
import com.supportapp.domain.UserPrincipal;
import com.supportapp.enums.Role;
import com.supportapp.exceptions.EmailExistException;
import com.supportapp.exceptions.EmailNotFoundException;
import com.supportapp.exceptions.UsernameExistException;
import com.supportapp.repository.UserRepository;
import com.supportapp.service.EmailService;
import com.supportapp.service.LoginAttempService;
import com.supportapp.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.supportapp.constant.UserImplConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private LoginAttempService loginAttempService;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private EmailService emailService;



    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, LoginAttempService loginAttempService, EmailService emailService){
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttempService = loginAttempService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(USER_NOT_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
        } else {
          //  try {
            //    validateLoginAttemp(user);
            //} catch (ExecutionException e) {
             //   throw new RuntimeException(e);
            //}
            user.setLastLogindDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            this.userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);

            return userPrincipal;
        }
    }


    private void validateLoginAttemp(User user) throws ExecutionException {
        if(user.isNotLocked()){
            if(this.loginAttempService.hasExceededMaxAttemps(user.getUsername())){
                user.setNotLocked(false);
            }else{
                user.setNotLocked(true);
            }
        }else{
            this.loginAttempService.evictUserFromLoginAttempCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateId());
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
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        this.userRepository.save(user);
        LOGGER.info("New user password " + password);
        this.emailService.SendNewPasswordEmail(firstName,password, email);

        return user;
    }
    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isActive, boolean isNonLocked, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User newUser = new User();
        newUser.setUserId(generateId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setJoinDate(new Date());
        newUser.setPassword(encodedPassword);
        newUser.setActive(true);
        newUser.setNotLocked(true);
        newUser.setRole(getRoleEnumName(role).name());
        newUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        newUser.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        this.userRepository.save(newUser);
        saveProfileImage(newUser, profileImage);
        return newUser;
    }


    @Override
    public User updateUser(String currentUsername, String firstName,String lastName,String username,String email,String role,boolean isActive,boolean isNonLocked,MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException {
       User currentUser =  validateNewUsernameAndEmail(currentUsername, username , email);
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        this.userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = this.userRepository.findUserByEmail(email);
        if(user == null){
            throw new EmailNotFoundException(USER_NOT_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        this.userRepository.save(user);
        this.emailService.SendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
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
        User userByNewEmail = findUserByEmail(newEmail);
        User userByNewUsername = findUserByUsername(newUsername);

        if(StringUtils.isNotBlank(currentUsername)){
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null){
                throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())){
                throw new UsernameExistException(USERNAME_ALREADY_EXIST);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())){
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
    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if(profileImage != null){
            Path userFolder = Paths.get(FileConstant.USER_FOLDER, user.getUsername()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
                LOGGER.info(FileConstant.DIRECTORY_CREATED);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + FileConstant.DOT + FileConstant.JPG_EXTENCION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + FileConstant.DOT  + FileConstant.JPG_EXTENCION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user));
            this.userRepository.save(user);
            LOGGER.info(FileConstant.FILE_SAVED_IN_FILESYSTEM);
        }
    }

    private String setProfileImageUrl(User user) {
      return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.USER_IMAGE_PATH + user.getUsername() + FileConstant.FORWARD_SLASH + user.getUsername() + FileConstant.DOT + FileConstant.JPG_EXTENCION).toUriString();
    }
    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_IMAGE_PATH + FileConstant.FORWARD_SLASH+ username).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
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
