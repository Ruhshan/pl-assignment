package xyz.ruhshan.pl.gateway.service;

import xyz.ruhshan.pl.gateway.request.SignInRequest;
import xyz.ruhshan.pl.gateway.request.SignUpRequest;
import xyz.ruhshan.pl.gateway.response.SignInResponse;
import xyz.ruhshan.pl.gateway.response.SignUpResponse;

public interface AuthService {
    SignUpResponse signUp(SignUpRequest request);
    SignInResponse signIn(SignInRequest request);
}
