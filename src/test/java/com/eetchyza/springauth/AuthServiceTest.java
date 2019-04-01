package com.eetchyza.springauth;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import com.eetchyza.springauth.api.GrantedAuthority;
import com.eetchyza.springauth.api.UserDetails;
import com.eetchyza.springauth.api.UserDetailsService;
import com.eetchyza.springauth.exceptions.NotAuthenticatedException;
import com.eetchyza.springauth.exceptions.PasswordExpiredException;
import com.eetchyza.springauth.exceptions.TokenExpiredException;
import com.eetchyza.springauth.exceptions.UsernameOrPasswordIncorrectException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceTest {
	private UserDetailsService userDetailsService;

	private AuthService authService;

	@Before
	public void setup() {
		userDetailsService = mock(UserDetailsService.class);
		authService = new AuthService(userDetailsService);
	}

	@Test(expected = UsernameOrPasswordIncorrectException.class)
	public void testLogin_noUser() throws UsernameOrPasswordIncorrectException {
		String username = "bad-bob";
		UserDetails user = createUser(4L, "STANDARD", "test-pass", LocalDateTime.now().plusHours(3));
		when(userDetailsService.loadUserByUsername(username)).thenReturn(null);

		authService.login(username, user.getPassword());
	}

	@Test(expected = UsernameOrPasswordIncorrectException.class)
	public void testLogin_incorrectPassword() throws UsernameOrPasswordIncorrectException {
		UserDetails user = createUser(6L, "STANDARD", authService.hashAndSalt("test-pass"), LocalDateTime.now().plusHours(3));
		when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

		authService.login(user.getUsername(), "bad-password");
	}

	@Test
	public void testLogin_success() throws UsernameOrPasswordIncorrectException {
		UserDetails user = createUser(6L, "STANDARD", authService.hashAndSalt("test-pass"), LocalDateTime.now().plusHours(3));
		when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

		Authentication actual = authService.login(user.getUsername(), "test-pass");
		assertThat(actual.isExpired()).isFalse();
	}

	@Test
	public void testPasswordsMatch_true(){
		String password = "test-password";
		assertThat(authService.passwordsMatch(password, authService.hashAndSalt(password))).isTrue();
	}

	@Test
	public void testPasswordsMatch_false(){
		assertThat(authService.passwordsMatch("test-password", authService.hashAndSalt("bad-password"))).isFalse();
	}

	@Test
	public void testSetCurrentUser() throws UsernameOrPasswordIncorrectException, PasswordExpiredException {
		UserDetails user = createUser(6L, "STANDARD", authService.hashAndSalt("test-pass"), LocalDateTime.now().plusHours(3));
		when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

		Authentication auth = authService.login(user.getUsername(), "test-pass");

		authService.setCurrentUser(auth.getAuthenticationToken());

		assertThat(SecurityContext.getCurrentUser()).isEqualTo(user);
	}

	@Test(expected = PasswordExpiredException.class)
	public void testSetCurrentUser_expiredUser() throws UsernameOrPasswordIncorrectException, PasswordExpiredException {
		UserDetails user = createUser(6L, "STANDARD", authService.hashAndSalt("test-pass"), LocalDateTime.now().minusHours(5), true);
		when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

		Authentication auth = authService.login(user.getUsername(), "test-pass");

		authService.setCurrentUser(auth.getAuthenticationToken());
	}

	@Test
	public void testCheckAuthenticated() throws NotAuthenticatedException, TokenExpiredException, UsernameOrPasswordIncorrectException {
		UserDetails user = createUser(6L, "STANDARD", authService.hashAndSalt("test-pass"), LocalDateTime.now().plusHours(3));
		when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

		Authentication auth = authService.login(user.getUsername(), "test-pass");

		authService.checkAuthenticated(auth.getAuthenticationToken());

		//If we get here we are authenticated
	}

	@Test(expected = NotAuthenticatedException.class)
	public void testCheckAuthenticated_notAuthenticated() throws NotAuthenticatedException, TokenExpiredException {
		UserDetails user = createUser(6L, "STANDARD", authService.hashAndSalt("test-pass"), LocalDateTime.now().plusHours(3));
		when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

		authService.checkAuthenticated("blabla");
	}

	private UserDetails createUser(long id, String auth, String password, LocalDateTime expires){
		return createUser(id, auth, password, expires, false);
	}

	private UserDetails createUser(long id, String auth, String password, LocalDateTime expires, boolean isTemp) {
		return new UserDetails() {
			@Override
			public long getId() {
				return id;
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return Collections.singletonList((GrantedAuthority) () -> auth);
			}

			@Override
			public String getPassword() {
				return password;
			}

			@Override
			public String getUsername() {
				return "test-user";
			}

			@Override
			public boolean isTemporaryPassword() {
				return isTemp;
			}

			@Override
			public LocalDateTime getExpires() {
				return expires;
			}
		};
	}
}