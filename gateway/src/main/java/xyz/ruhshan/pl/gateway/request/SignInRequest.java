package xyz.ruhshan.pl.gateway.request;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}
