package com.eetchyza.springauth;

import com.eetchyza.springauth.annotations.AllowAnon;
import com.eetchyza.springauth.annotations.AllowRoles;
import com.eetchyza.springauth.api.UserDetails;
import com.eetchyza.springauth.api.UserDetailsService;
import com.eetchyza.springauth.exceptions.NotAuthenticatedException;
import com.eetchyza.springauth.exceptions.NotAuthorisedException;
import com.eetchyza.springauth.exceptions.PasswordExpiredException;
import com.eetchyza.springauth.exceptions.TokenExpiredException;
import com.eetchyza.springauth.exceptions.UsernameOrPasswordIncorrectException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final Map<String, Authentication> authenticationMap;
    private final Map<String, UserDetails> loggedInMap;

    @Autowired
    public AuthService(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
        this.authenticationMap = new HashMap<>();
        this.loggedInMap = new HashMap<>();
    }

    public Authentication login(String username, String password) throws UsernameOrPasswordIncorrectException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails == null || !passwordsMatch(password, userDetails.getPassword())){
            throw new UsernameOrPasswordIncorrectException();
        }

        Authentication authentication = new Authentication(generateToken(), generateToken(), LocalDateTime.now().plusHours(1), userDetails.getAuthorities(), username, userDetails.getId());
        authenticationMap.put(authentication.getAuthenticationToken(), authentication);
        loggedInMap.put(authentication.getAuthenticationToken(), userDetails);

        return authentication;
    }

    public boolean passwordsMatch(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }

    public void logout(String token){
        authenticationMap.remove(token);
        loggedInMap.remove(token);
    }

    public void setCurrentUser(String token) throws PasswordExpiredException {
        UserDetails userDetails = loggedInMap.get(token);

        if(userDetails != null && (userDetails.isTemporaryPassword() && userDetails.getExpires().isBefore(LocalDateTime.now()))){
            throw new PasswordExpiredException();
        }

        SecurityContext.setCurrentUser(userDetails);
    }

    public Authentication refresh(String token, String refreshToken){
        Authentication authentication = authenticationMap.get(token);
        Authentication newAuth = null;

        if(authentication.isRefreshToken(refreshToken)){
            authenticationMap.remove(token);
            UserDetails loggedInUser = loggedInMap.get(token);

            newAuth = new Authentication(generateToken(), generateToken(), LocalDateTime.now().plusHours(1), authentication.getRoles(), loggedInUser.getUsername(), loggedInUser.getId());
            authenticationMap.put(newAuth.getAuthenticationToken(), newAuth);
            loggedInMap.remove(token);
            loggedInMap.put(newAuth.getAuthenticationToken(), loggedInUser);
        }

        return newAuth;
    }

    public void checkAuthenticated(String token) throws NotAuthenticatedException, TokenExpiredException {
        Authentication authentication = authenticationMap.get(token);

        if(authentication == null){
            throw new NotAuthenticatedException();
        }

        if(authentication.isExpired()){
            throw new TokenExpiredException();
        }
    }

    public void checkIsAuthorised(String token, Method method) throws NotAuthorisedException {
        Authentication authentication = authenticationMap.get(token);

        if(!method.isAnnotationPresent(AllowAnon.class) && !authentication.hasRoles(method.getAnnotation(AllowRoles.class).value())){
            throw new NotAuthorisedException();
        }
    }

    public String hashAndSalt(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String generateToken(){
        int length = 20;

        String[] characterGroups = new String[4];
        characterGroups[0]=("abcdefghjkmnpqrstuvwxyz");
        characterGroups[1]=("ABCDEFGHJKMNPQRSTWUVXYZ");
        characterGroups[2]=("23456789");
        characterGroups[3]=("!-+#");

        Random rng = new Random();

        List<String> chars = new ArrayList<>();
        for(String characters : characterGroups){
            char[] text = new char[length];
            for (int i = 0; i < length; i++) {
                text[i] = characters.charAt(rng.nextInt(characters.length()));
            }

            chars.add((new String(text)));
        }

        StringBuilder characters = new StringBuilder();

        for(String character : chars){
            characters.append(character);
        }

        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }

        String generated = new String(text);

        if(authenticationMap.containsKey(generated)){
            generated = generateToken();
        }

        return generated;
    }
}
