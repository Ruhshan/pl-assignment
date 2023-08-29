package xyz.ruhshan.pl.gateway.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import xyz.ruhshan.pl.common.enums.Role;

@Data
@Builder
@AllArgsConstructor
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}
