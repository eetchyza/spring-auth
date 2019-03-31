package com.eetchyza.springauth.exceptions;

public class NotAuthenticatedException extends Exception {
    public NotAuthenticatedException(){
        super("User is not authenticated");
    }
}
