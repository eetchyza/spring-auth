package com.eetchyza.springauth.exceptions;

public class PasswordExpiredException extends Exception{
    public PasswordExpiredException(){
        super("Password expired");
    }
}
