package io.github.eetchyza.springauth;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.eetchyza.springauth.annotations.AllowAnon;
import io.github.eetchyza.springauth.annotations.AllowRoles;
import io.github.eetchyza.springauth.api.UserDetails;
import io.github.eetchyza.springauth.api.UserDetailsService;
import io.github.eetchyza.springauth.exceptions.NotAuthenticatedException;
import io.github.eetchyza.springauth.exceptions.NotAuthorisedException;
import io.github.eetchyza.springauth.exceptions.PasswordExpiredException;
import io.github.eetchyza.springauth.exceptions.TokenExpiredException;
import io.github.eetchyza.springauth.exceptions.UsernameOrPasswordIncorrectException;
import org.mindrot.jbcrypt.BCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A service for providing authentication and authorisation logic
 *
 * @author Dan Williams
 * @version 1.0.0
 * @since 2019-04-04
 */
@Component
public class AuthService {

	private final UserDetailsService userDetailsService;

	private final Map<String, Authentication> authenticationMap;

	private final Map<String, UserDetails> loggedInMap;

	@Autowired
	public AuthService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
		this.authenticationMap = new HashMap<>();
		this.loggedInMap = new HashMap<>();
	}

	/**
	 * This method is used to retrieve a users details and validates the given password.
	 * Once retrieved and validated an authentication object is created and stored for later authorization
	 *
	 * @param username Users username
	 * @param password Users un-hashed password
	 * @return {@link Authentication Authentication} Returns authentication details
	 * @exception UsernameOrPasswordIncorrectException Exception thrown when username or password is incorrect
	 * @see UsernameOrPasswordIncorrectException
	 */
	public Authentication login(String username, String password) throws UsernameOrPasswordIncorrectException {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (userDetails == null || !passwordsMatch(password, userDetails.getPassword())) {
			throw new UsernameOrPasswordIncorrectException();
		}

		Authentication authentication = new Authentication(generateToken(), generateToken(), LocalDateTime.now().plusHours(1), userDetails.getAuthorities(), username, userDetails.getId());
		authenticationMap.put(authentication.getAuthenticationToken(), authentication);
		loggedInMap.put(authentication.getAuthenticationToken(), userDetails);

		return authentication;
	}

	/**
	 * This method is used to check is a plain text password matched a hashed password.
	 *
	 * @param password Plain text password
	 * @param hashedPassword Hashed password
	 * @return boolean Returns true if the hash and password match
	 */
	public boolean passwordsMatch(String password, String hashedPassword) {
		return BCrypt.checkpw(password, hashedPassword);
	}

	/**
	 * This method is used to remove a users authorization details from the store
	 *
	 * @param token A users auth token
	 */
	public void logout(String token) {
		authenticationMap.remove(token);
		loggedInMap.remove(token);
	}

	/**
	 * This method is used to add a stored user to the security context.
	 *
	 * @param token Users auth token
	 * @exception PasswordExpiredException  Exception thrown when users password has expired
	 * @see PasswordExpiredException
	 */
	public void setCurrentUser(String token) throws PasswordExpiredException {
		UserDetails userDetails = loggedInMap.get(token);

		if (userDetails != null && (userDetails.isTemporaryPassword() && userDetails.getExpires().isBefore(LocalDateTime.now()))) {
			throw new PasswordExpiredException();
		}

		SecurityContext.setCurrentUser(userDetails);
	}

	/**
	 * This method is used to regenerate a users stored authentication details
	 *
	 * @param token Users auth token
	 * @param refreshToken Users refresh token
	 * @return {@link Authentication Authentication} Returns authentication details
	 */
	public Authentication refresh(String token, String refreshToken) {
		Authentication authentication = authenticationMap.get(token);
		Authentication newAuth = null;

		if (authentication.isRefreshToken(refreshToken)) {
			authenticationMap.remove(token);
			UserDetails loggedInUser = loggedInMap.get(token);

			newAuth = new Authentication(generateToken(), generateToken(), LocalDateTime.now().plusHours(1), authentication.getRoles(), loggedInUser.getUsername(), loggedInUser.getId());
			authenticationMap.put(newAuth.getAuthenticationToken(), newAuth);
			loggedInMap.remove(token);
			loggedInMap.put(newAuth.getAuthenticationToken(), loggedInUser);
		}

		return newAuth;
	}

	/**
	 * This method is used to check if a user is authenticated
	 *
	 * @param token Users auth token
	 * @exception NotAuthenticatedException Exception thrown when there is no stored authentication details
	 * @exception TokenExpiredException Exception thrown when the auth has expired
	 * @see NotAuthenticatedException
	 * @see TokenExpiredException
	 */
	public void checkAuthenticated(String token) throws NotAuthenticatedException, TokenExpiredException {
		Authentication authentication = authenticationMap.get(token);

		if (authentication == null) {
			throw new NotAuthenticatedException();
		}

		if (authentication.isExpired()) {
			throw new TokenExpiredException();
		}
	}

	public void checkIsAuthorised(String token, Method method) throws NotAuthorisedException {
		Authentication authentication = authenticationMap.get(token);

		if (!method.isAnnotationPresent(AllowAnon.class) && !authentication.hasRoles(method.getAnnotation(AllowRoles.class).value())) {
			throw new NotAuthorisedException();
		}
	}

	public String hashAndSalt(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public String generateToken() {
		int length = 20;

		String[] characterGroups = new String[4];
		characterGroups[0] = ("abcdefghjkmnpqrstuvwxyz");
		characterGroups[1] = ("ABCDEFGHJKMNPQRSTWUVXYZ");
		characterGroups[2] = ("23456789");
		characterGroups[3] = ("!-+#");

		Random rng = new Random();

		List<String> chars = new ArrayList<>();
		for (String characters : characterGroups) {
			char[] text = new char[length];
			for (int i = 0; i < length; i++) {
				text[i] = characters.charAt(rng.nextInt(characters.length()));
			}

			chars.add((new String(text)));
		}

		StringBuilder characters = new StringBuilder();

		for (String character : chars) {
			characters.append(character);
		}

		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}

		String generated = new String(text);

		if (authenticationMap.containsKey(generated)) {
			generated = generateToken();
		}

		return generated;
	}
}
