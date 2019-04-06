package com.eetchyza.springauth.exceptions;

/**
 * UsernameOrPasswordIncorrectException
 *
 * @author Dan Williams
 * @version 1.0.0
 * @since 2019-04-06
 */
public class UsernameOrPasswordIncorrectException extends Exception {
	public UsernameOrPasswordIncorrectException() {
		super("Username or password is incorrect");
	}
}
