package com.supportapp.constant;

public class CorsConstant {
    public static final String[] ALLOWED_HEADERS = {"Origin", "Access-Control-Allow-Origin","Content-Type", "Accept", "Jwt-Token", "Authorization", "Origin, Accept","X-Requested-With","Access-Control-Request-Method", "Acces-Control-Request-Headers"};
    public static final String[] EXPOSED_HEADERS = {"Origin","Content-Type","Accept", "Jwt-Token", "Authorization","Access-Control-Allow-Origin","Access-Control-Allow-Credentials"};
    public static final String[] HTTP_METHODS = {"GET", "POST", "PUT", "DELETE"};
    public static final String SUPPORTAPPWEB_URL = "https://supportapp-production.up.railway.app";
}
