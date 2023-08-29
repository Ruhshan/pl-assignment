package xyz.ruhshan.pl.gateway.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import xyz.ruhshan.pl.common.enums.Role;

@Data
@Builder
@AllArgsConstructor
public class SignUpRequest {
    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;
    @Email
    private String email;
    private String password;
    private Role role;
}
