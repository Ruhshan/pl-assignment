package xyz.ruhshan.pl.gateway.service;


import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import xyz.ruhshan.pl.common.entity.User;
import xyz.ruhshan.pl.common.enums.Role;
import xyz.ruhshan.pl.common.repository.UserRepository;
import xyz.ruhshan.pl.gateway.request.SignUpRequest;
import xyz.ruhshan.pl.gateway.response.SignUpResponse;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        var user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();

        try{
            userRepository.save(user);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create user");
        }

        return new SignUpResponse("User created successfully");



    }
}
