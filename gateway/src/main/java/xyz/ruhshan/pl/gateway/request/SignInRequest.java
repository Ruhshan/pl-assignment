package xyz.ruhshan.pl.gateway.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInRequest {
    @Email
    private String email;
    @Size(min = 10, max = 50)
    private String password;
}
