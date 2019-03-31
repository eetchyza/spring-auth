package com.eetchyza.springauth;

import com.eetchyza.springauth.api.UserDetails;

public class SecurityContext {
    private static volatile ThreadLocal<UserDetails> user = new ThreadLocal<>();

    public static synchronized UserDetails getCurrentUser(){
        return user.get();
    }

    public static synchronized void setCurrentUser(UserDetails userDetails){
        user.set(userDetails);
    }

    public static synchronized void clear(){
        user.remove();
    }
}
