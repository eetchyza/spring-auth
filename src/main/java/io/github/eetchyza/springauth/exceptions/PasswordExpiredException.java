package io.github.eetchyza.springauth.exceptions;

/**
 * PasswordExpiredException
 *
 * @author Dan Williams
 * @version 1.0.0
 * @since 2019-04-06
 */
public class PasswordExpiredException extends Exception {
	public PasswordExpiredException() {
		super("Password expired");
	}
}
