package com.eetchyza.springauth;

import com.eetchyza.springauth.api.UserDetails;

/**
 * A thread safe Security Context used to track the current logged in users.
 *
 * @author Dan Williams
 * @version 1.0.0
 * @since 2019-04-04
 */
public class SecurityContext {
	private static volatile ThreadLocal<UserDetails> user = new ThreadLocal<>();

	private SecurityContext() {
		throw new IllegalStateException("Security context can not be initialised");
	}

	/**
	 * This method is used to get the current user
	 *
	 * @return {@link com.eetchyza.springauth.api.UserDetails UserDetails} This returns the current user associated with the current thread.
	 */
	public static synchronized UserDetails getCurrentUser() {
		return user.get();
	}

	/**
	 * This adds a user to the security context for this thread
	 *
	 * @param userDetails {@link com.eetchyza.springauth.api.UserDetails UserDetails} User to be set.
	 */
	public static synchronized void setCurrentUser(UserDetails userDetails) {
		user.set(userDetails);
	}

	/**
	 * Removes the user associated with this thread from the Security Context
	 * */
	public static synchronized void clear() {
		user.remove();
	}
}
