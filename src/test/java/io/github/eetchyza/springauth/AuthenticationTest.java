package io.github.eetchyza.springauth;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import io.github.eetchyza.springauth.api.GrantedAuthority;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthenticationTest {

	private String authenticationToken;

	private String refreshToken;

	private LocalDateTime expire;

	private Collection<? extends GrantedAuthority> roles;

	private String username;

	private long id;

	private Authentication authentication;

	@Before
	public void setup() {
		authenticationToken = "test-auth-token";
		refreshToken = "test-refresh-token";
		expire = LocalDateTime.now().plusHours(1);
		roles = Collections.singletonList((GrantedAuthority) () -> "STANDARD");
		username = "test-user";
		id = 5L;
		authentication = new Authentication(authenticationToken, refreshToken, expire, roles, username, id);
	}

	@Test
	public void testIsRefreshToken_true() {
		assertTrue(authentication.isRefreshToken(refreshToken));
	}

	@Test
	public void testIsRefreshToken_false() {
		assertFalse(authentication.isRefreshToken("234097ghhjh"));
	}

	@Test
	public void testIsExpired_true() {
		authentication = new Authentication(authenticationToken, refreshToken, LocalDateTime.now().minusHours(2), roles, username, id);
		assertTrue(authentication.isExpired());
	}

	@Test
	public void testIsExpired_false() {
		assertFalse(authentication.isExpired());
	}

	@Test
	public void testHasRoles_true() {
		assertTrue(authentication.hasRoles(new String[] { "STANDARD" }));
	}

	@Test
	public void testHasRoles_false() {
		assertFalse(authentication.hasRoles(new String[] { "ADMIN" }));
	}
}