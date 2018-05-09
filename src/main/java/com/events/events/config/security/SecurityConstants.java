package com.events.events.config.security;

public enum SecurityConstants {
    SECRETKEY("SecretKeyToGenJWTs"),
    EXPIRATIONTIME("864_000_000"),
    TOKEN_PREFIX("Bearer "),
    HEADER_STRING("Authorization"),
    SIGN_UP_URL("/register");

    private String constant;

     SecurityConstants(String constant){
        this.constant = constant;
    }

    public String getConstant(){
         return constant;
    }
}
