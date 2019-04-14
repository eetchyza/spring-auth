package io.github.eetchyza.springauth.api;

public interface UserDetailsService {
	UserDetails loadUserByUsername(String username);
}
