package com.eetchyza.springauth.exceptions;

public class UsernameOrPasswordIncorrectException extends Exception {
    public UsernameOrPasswordIncorrectException(){
        super("Username or password is incorrect");
    }
}
