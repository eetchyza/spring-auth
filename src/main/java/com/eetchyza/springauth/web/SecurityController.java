package com.eetchyza.springauth.web;

import com.eetchyza.springauth.AuthService;
import com.eetchyza.springauth.Authentication;
import com.eetchyza.springauth.annotations.AllowAnon;
import com.eetchyza.springauth.exceptions.UsernameOrPasswordIncorrectException;
import com.eetchyza.springauth.web.dto.LoginDto;
import com.eetchyza.springauth.web.dto.RefreshDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/security", consumes = "application/json", produces = "application/json")
@Component
public class SecurityController {
    private final AuthService authService;

    @Autowired
    public SecurityController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    @AllowAnon
    public @ResponseBody Authentication login(@RequestBody LoginDto loginDto) throws UsernameOrPasswordIncorrectException {
        return authService.login(loginDto.getUsername(), loginDto.getPassword());
    }

    @GetMapping("/logout")
    @AllowAnon
    public void logout(@RequestHeader("TOKEN") String token){
        authService.logout(token);
    }

    @PostMapping("/refresh")
    @AllowAnon
    public @ResponseBody Authentication refresh(@RequestBody RefreshDto refreshDto) {
        return authService.refresh(refreshDto.getToken(), refreshDto.getRefreshToken());
    }
}
