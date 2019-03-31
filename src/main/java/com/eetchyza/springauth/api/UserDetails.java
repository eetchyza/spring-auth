package com.eetchyza.springauth.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

public interface UserDetails extends Serializable {

    long getId();

    Collection<? extends GrantedAuthority> getAuthorities();

    String getPassword();

    String getUsername();

    boolean isTemporaryPassword();

    LocalDateTime getExpires();
}
