package com.eetchyza.springauth.exceptions;

/**
 * TokenExpiredException
 *
 * @author Dan Williams
 * @version 1.0.0
 * @since 2019-04-06
 */
public class TokenExpiredException extends Exception {
    public TokenExpiredException(){
        super("Token has expired");
    }
}
