package xyz.ruhshan.pl.gateway.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import xyz.ruhshan.pl.common.entity.User;
import xyz.ruhshan.pl.common.enums.Role;
import xyz.ruhshan.pl.common.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import xyz.ruhshan.pl.gateway.request.SignInRequest;
import xyz.ruhshan.pl.gateway.request.SignUpRequest;
import xyz.ruhshan.pl.gateway.response.SignInResponse;
import xyz.ruhshan.pl.gateway.response.SignUpResponse;
import xyz.ruhshan.pl.gateway.service.AuthServiceImpl;
import xyz.ruhshan.pl.gateway.service.JwtService;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp() {
        SignUpRequest signUpRequest = new SignUpRequest("John", "Doe", "john@example.com", "password",
            Role.USER);
        User user = User.builder()
            .firstName(signUpRequest.getFirstName())
            .lastName(signUpRequest.getLastName())
            .email(signUpRequest.getEmail())
            .password("encodedPassword")
            .role(Role.USER)
            .build();

        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        SignUpResponse signUpResponse = authService.signUp(signUpRequest);

        assertNotNull(signUpResponse);
        assertEquals("User created successfully", signUpResponse.getMessage());

        verify(passwordEncoder).encode(signUpRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testSignIn() {
        SignInRequest signInRequest = new SignInRequest("john@example.com", "password");
        User user = User.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .password("encodedPassword")
            .role(Role.USER)
            .build();

        when(userRepository.findByEmail(signInRequest.getEmail())).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user.getEmail())).thenReturn("mockedToken");

        SignInResponse signInResponse = authService.signIn(signInRequest);

        assertNotNull(signInResponse);
        assertEquals("mockedToken", signInResponse.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(signInRequest.getEmail());
        verify(jwtService).generateToken(user.getEmail());
    }
}
