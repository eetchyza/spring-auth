package com.eetchyza.springauth.exceptions;

/**
 * NotAuthenticatedException
 *
 * @author Dan Williams
 * @version 1.0.0
 * @since 2019-04-06
 */
public class NotAuthenticatedException extends Exception {
    public NotAuthenticatedException(){
        super("User is not authenticated");
    }
}
