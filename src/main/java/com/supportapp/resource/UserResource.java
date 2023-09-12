package com.supportapp.resource;

import com.supportapp.constant.FileConstant;
import com.supportapp.domain.HttpResponse;
import com.supportapp.domain.Key;
import com.supportapp.domain.User;
import com.supportapp.domain.UserPrincipal;
import com.supportapp.exceptions.EmailExistException;
import com.supportapp.exceptions.EmailNotFoundException;
import com.supportapp.exceptions.ExceptionHandling;
import com.supportapp.exceptions.UsernameExistException;
import com.supportapp.service.UserService;
import com.supportapp.utility.JWTTokenProvider;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.supportapp.constant.SecurityConstant.JWT_TOKEN_HEADER;
@Controller
@RestController
@RequestMapping(value = "/user")
public class UserResource extends ExceptionHandling {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws Exception {
        User loginUser =  this.userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity(loginUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = this.userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }
    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked")String isNonLocked,
                                           @RequestParam(value="profileImage", required = false)MultipartFile profileImage) throws EmailExistException, IOException, UsernameExistException {

        User newUser = this.userService.addNewUser(firstName, lastName,username,email,role,Boolean.parseBoolean(isActive),Boolean.parseBoolean(isNonLocked), profileImage);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("firstName") String firstName,
                                           @RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked")String isNonLocked,
                                           @RequestParam(value="profileImage", required = false)MultipartFile profileImage) throws EmailExistException, IOException, UsernameExistException {

        User newUser = this.userService.updateUser(currentUsername,firstName, lastName,username,email,role,Boolean.parseBoolean(isActive),Boolean.parseBoolean(isNonLocked), profileImage);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username){
        User userFound = this.userService.findUserByUsername(username);
        return new ResponseEntity<>(userFound, HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("/list")
    public ResponseEntity<List<User>> getUsers(){
        List<User> usersList = this.userService.getusers();
        return new ResponseEntity<>(usersList, HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") Long id){
        this.userService.deleteUser(id);
        return response(HttpStatus.OK, "User deleted succefully");
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@PathVariable("profileImage") MultipartFile profileImage,
                                               @RequestBody User user) throws EmailExistException, IOException, UsernameExistException {
        User newUserImg = this.userService.updateProfileImage(user.getUsername(), profileImage);
        return new ResponseEntity(newUserImg, HttpStatus.OK);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws Exception {
        this.userService.resetPassword(email);
        return response(HttpStatus.OK, "Email sent to: " + email);
    }
    @GetMapping(value = "/updateProfileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> getProfileImage(@RequestParam("username") String username,
                                  @RequestParam("fileName") MultipartFile fileName) throws IOException, EmailExistException, UsernameExistException {
        User user = this.userService.updateProfileImage(username, fileName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping(path = "/image/profile/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getTempProfileImage(@PathVariable("username") String username) throws IOException {

        String imageUrl = FileConstant.TEMP_PROFILE_IMAGE_BASE_URL
                + username;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);

    }

    private ResponseEntity<HttpResponse> response(HttpStatus status, String message) {
        HttpResponse body = new HttpResponse(new Date(), status.value(), status, status.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, status);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, this.jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        this.authenticationManager.authenticate(authToken);
    }
}
