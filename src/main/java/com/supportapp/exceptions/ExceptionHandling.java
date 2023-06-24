package com.supportapp.exceptions;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.supportapp.domain.HttpResponse;
import com.supportapp.domain.User;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionHandling{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    public static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint";
    public static final String INTERNAL_SERVER_ERROR_MSG = "An error ocurred while processing the request";
    public static final String INCORRECT_CREDENTIAL = "Username / password incorrect. Please try again";
    public static final String ACCOUNT_DISABLED = "Your account has been disableds";
    public static final String ERROR_PROCESSING_FILE = "Error ocurred while processing file";
    public static final String NOT_ENOUGHT_PERMISSION = "You do not have enought permission";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity accountDisabledException(DisabledException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity badCredentialException(BadCredentialsException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIAL);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accessDeniedException(AccessDeniedException e){
        return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGHT_PERMISSION);
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity lockedException(LockedException e){
        return createHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity tokenExpiredException(TokenExpiredException e){
        return createHttpResponse(HttpStatus.UNAUTHORIZED, e.getMessage().toUpperCase());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity emailExistException(EmailExistException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage().toUpperCase());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity usernameExistExceptionn(UsernameExistException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage().toUpperCase());
    }
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity emailNotFoundException(EmailNotFoundException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity userNotFoundException(UsernameNotFoundException e){
        return createHttpResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity <HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException e){
        HttpMethod supportedMethod = HttpMethod.valueOf(Objects.requireNonNull(Arrays.stream(e.getSupportedMethods()).iterator().next()));
        return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }
    @ExceptionHandler(NoResultException.class)
    public ResponseEntity notFoundException(NoResultException e){
        LOGGER.error(e.getMessage());
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity internalServerErrorException(Exception e){
        e.printStackTrace();
        LOGGER.error(e.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity iOException(IOException e){
        LOGGER.error(e.getMessage());
        return createHttpResponse(HttpStatus.BAD_REQUEST, ERROR_PROCESSING_FILE);
    }
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message){
        HttpResponse httpResponse = new HttpResponse(new Date(),httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),message.toUpperCase());
        return new ResponseEntity<>(httpResponse, httpStatus);

    }

}
