package irusri.assignment.todo_app.services;

import irusri.assignment.todo_app.dtos.LoginUserDTO;
import irusri.assignment.todo_app.dtos.RegisterUserDTO;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.exceptions.ResourceAlreadyExistsException;
import irusri.assignment.todo_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Test case for a successful signup
    @Test
    void testSignupSuccess() {
        RegisterUserDTO registerUserDTO = new RegisterUserDTO("user@example.com", "password", "first", "last");

        when(userRepository.findByEmail(registerUserDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerUserDTO.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User()
                .setEmail(registerUserDTO.getEmail())
                .setPassword("encodedPassword")
                .setFirstName(registerUserDTO.getFirstName())
                .setLastName(registerUserDTO.getLastName());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authenticationService.registerUser(registerUserDTO);

        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("first", result.getFirstName());
        assertEquals("last", result.getLastName());
    }

    //Test case for attempting to sign up when a client already exists
    @Test
    void testSignupClientAlreadyExists() {
        RegisterUserDTO registerClientDto = new RegisterUserDTO("client@example.com", "password", "Client", "User");

        when(userRepository.findByEmail(registerClientDto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(ResourceAlreadyExistsException.class, () -> authenticationService.registerUser(registerClientDto));
    }

    //Test case for a successful authentication
    @Test
    void testAuthenticateSuccess() {
        LoginUserDTO loginUserDTO = new LoginUserDTO("client@example.com", "password");

        when(userRepository.findByEmail(loginUserDTO.getEmail())).thenReturn(Optional.of(new User()));

        authenticationService.authenticate(loginUserDTO);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(
                loginUserDTO.getEmail(), loginUserDTO.getPassword()
        ));
    }

    //Test case for authentication failure when the client is not found
    @Test
    void testAuthenticateClientNotFound() {
        LoginUserDTO loginUserDTO = new LoginUserDTO("client@example.com", "password");

        when(userRepository.findByEmail(loginUserDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginUserDTO));
    }
}

