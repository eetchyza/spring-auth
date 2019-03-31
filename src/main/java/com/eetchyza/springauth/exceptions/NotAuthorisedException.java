package com.eetchyza.springauth.exceptions;

public class NotAuthorisedException extends Exception {
    public NotAuthorisedException(){
        super("Not authorised");
    }
}
