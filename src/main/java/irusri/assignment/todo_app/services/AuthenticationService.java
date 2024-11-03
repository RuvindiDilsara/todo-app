package irusri.assignment.todo_app.services;

import irusri.assignment.todo_app.dtos.LoginUserDTO;
import irusri.assignment.todo_app.dtos.RegisterUserDTO;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.exceptions.ResourceAlreadyExistsException;
import irusri.assignment.todo_app.exceptions.TodoServiceException;
import irusri.assignment.todo_app.exceptions.UserNotFoundExeption;
import irusri.assignment.todo_app.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // method for user registering
    public User registerUser(RegisterUserDTO input) {
        logger.info("Attempting to register user with email: {}", input.getEmail());

        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            logger.warn("User already exists with email: {}", input.getEmail());
            throw new ResourceAlreadyExistsException("User already exists with this email: " + input.getEmail());
        }

        User user = new User()
                .setFirstName(input.getFirstName())
                .setLastName(input.getLastName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()));

        try {
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully with ID: {}", savedUser.getId());
            return savedUser;
        } catch (Exception ex) {
            logger.error("Error registering user: {}", ex.getMessage(), ex);
            throw new TodoServiceException("Error registering user", ex);
        }
    }

    // Method for user authentication
    public User authenticate(LoginUserDTO input) {
        logger.info("Attempting to authenticate user with email: {}", input.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );

            return userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> {
                        logger.warn("User not found with email: {}", input.getEmail());
                        return new UserNotFoundExeption("User not found with email: " + input.getEmail());
                    });
        } catch (BadCredentialsException ex) {
            logger.error("Invalid email or password for user: {}", input.getEmail());
            throw new BadCredentialsException("Invalid email or password", ex);
        } catch (Exception ex) {
            logger.error("Error during authentication: {}", ex.getMessage(), ex);
            throw new TodoServiceException("Error during authentication", ex);
        }
    }
}