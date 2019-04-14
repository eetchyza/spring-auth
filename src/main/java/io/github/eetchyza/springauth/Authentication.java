package io.github.eetchyza.springauth;

import java.time.LocalDateTime;
import java.util.Collection;

import io.github.eetchyza.springauth.api.GrantedAuthority;

/**
 * Holds authentication details
 *
 * @author Dan Williams
 * @version 1.0.0
 * @since 2019-04-06
 */
public class Authentication {
	private String authenticationToken;

	private String refreshToken;

	private LocalDateTime expire;

	private Collection<? extends GrantedAuthority> roles;

	private String username;

	private long id;

	Authentication(String authenticationToken, String refreshToken, LocalDateTime expire, Collection<? extends GrantedAuthority> roles, String username, long id) {
		this.authenticationToken = authenticationToken;
		this.refreshToken = refreshToken;
		this.expire = expire;
		this.roles = roles;
		this.username = username;
		this.id = id;
	}

	boolean isRefreshToken(String refreshToken) {
		return refreshToken.equals(this.refreshToken);
	}

	Collection<? extends GrantedAuthority> getRoles() {
		return roles;
	}

	String getAuthenticationToken() {
		return authenticationToken;
	}

	boolean isExpired() {
		return expire.isBefore(LocalDateTime.now());
	}

	boolean hasRoles(String[] values) {

		for (GrantedAuthority authority : roles) {
			for (String value : values) {
				if (value.equals(authority.getAuthority())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return authenticationToken;
	}
}
