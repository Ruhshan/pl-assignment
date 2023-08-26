package xyz.ruhshan.pl.gateway.service;

import xyz.ruhshan.pl.gateway.request.SignUpRequest;
import xyz.ruhshan.pl.gateway.response.SignUpResponse;

public interface AuthService {
    SignUpResponse signUp(SignUpRequest request);
}
