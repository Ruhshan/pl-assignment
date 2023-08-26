package xyz.ruhshan.pl.gateway.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.ruhshan.pl.gateway.request.SignInRequest;
import xyz.ruhshan.pl.gateway.request.SignUpRequest;
import xyz.ruhshan.pl.gateway.response.SignInResponse;
import xyz.ruhshan.pl.gateway.response.SignUpResponse;
import xyz.ruhshan.pl.gateway.service.AuthService;

@RestController
public class AuthController extends ApiV1Controller{
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping( "/auth/signup")
    public SignUpResponse signUp(@RequestBody SignUpRequest signUpRequest){
        return authService.signUp(signUpRequest);
    }

    @PostMapping("/auth/signin")
    public SignInResponse signIn(@RequestBody SignInRequest signInRequest) {
        return authService.signIn(signInRequest);
    }

}
