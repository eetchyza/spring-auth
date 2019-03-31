package com.eetchyza.springauth.exceptions;

public class TokenExpiredException extends Exception {
    public TokenExpiredException(){
        super("Token has expired");
    }
}
