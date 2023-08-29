package xyz.ruhshan.pl.gateway.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.ruhshan.pl.common.enums.Role;
import xyz.ruhshan.pl.gateway.validation.ValidPassword;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;
    @Email
    private String email;
    @ValidPassword
    private String password;
    private Role role;
}
