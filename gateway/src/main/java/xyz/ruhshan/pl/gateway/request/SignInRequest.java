package xyz.ruhshan.pl.gateway.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.ruhshan.pl.gateway.validation.ValidPassword;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    @Email
    private String email;
    @ValidPassword
    private String password;
}
