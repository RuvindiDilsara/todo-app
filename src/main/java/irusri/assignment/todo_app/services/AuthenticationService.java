package irusri.assignment.todo_app.services;

import irusri.assignment.todo_app.dtos.LoginUserDTO;
import irusri.assignment.todo_app.dtos.RegisterUserDTO;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.exceptions.ResourceAlreadyExistsException;
import irusri.assignment.todo_app.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
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

    public User registerUser(RegisterUserDTO input) {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User already exists with this email: " + input.getEmail());
        }

        User user = new User()
                .setFirstName(input.getFirstName())
                .setLastName(input.getLastName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}