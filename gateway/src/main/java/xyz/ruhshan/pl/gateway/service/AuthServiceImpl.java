package xyz.ruhshan.pl.gateway.service;


import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import xyz.ruhshan.pl.common.entity.User;
import xyz.ruhshan.pl.common.enums.Role;
import xyz.ruhshan.pl.common.repository.UserRepository;
import xyz.ruhshan.pl.gateway.request.SignInRequest;
import xyz.ruhshan.pl.gateway.request.SignUpRequest;
import xyz.ruhshan.pl.gateway.response.SignInResponse;
import xyz.ruhshan.pl.gateway.response.SignUpResponse;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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

    @Override
    public SignInResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        String token = jwtService.generateToken(user.getEmail());
        return new SignInResponse(token);
    }
}
