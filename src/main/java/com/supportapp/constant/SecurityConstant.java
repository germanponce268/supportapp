package com.supportapp.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432000000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String LADEDEV = "LaDeDev";
    public static final String LADEDEV_ADMINISTRATION ="User Management Portal";
    public static final String AUTHORITIES = "authorities" ;
    public static final String FORBIDDEN_MESSAGE = "you need to log in to access this page";
    public static final String ACCES_DENIED_MESSAGE= "You do not have permission to access this page";
    public static final String OPTION_HTTP_METHOD= "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/user/login", "/user/register", "/user/resetPassword/**", "/user/image/**"};






}
