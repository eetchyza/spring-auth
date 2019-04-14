package io.github.eetchyza.springauth.exceptions;

/**
 * NotAuthorisedException
 *
 * @author Dan Williams
 * @version 1.0.0
 * @since 2019-04-06
 */
public class NotAuthorisedException extends Exception {
    public NotAuthorisedException(){
        super("Not authorised");
    }
}
