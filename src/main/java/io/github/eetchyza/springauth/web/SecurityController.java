package io.github.eetchyza.springauth.web;

import io.github.eetchyza.springauth.AuthService;
import io.github.eetchyza.springauth.Authentication;
import io.github.eetchyza.springauth.annotations.AllowAnon;
import io.github.eetchyza.springauth.exceptions.UsernameOrPasswordIncorrectException;
import io.github.eetchyza.springauth.web.dto.LoginDto;
import io.github.eetchyza.springauth.web.dto.RefreshDto;

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
    public @ResponseBody
    Authentication login(@RequestBody LoginDto loginDto) throws UsernameOrPasswordIncorrectException {
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
