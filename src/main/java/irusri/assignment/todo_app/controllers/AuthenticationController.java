package irusri.assignment.todo_app.controllers;

import irusri.assignment.todo_app.dtos.LoginUserDTO;
import irusri.assignment.todo_app.dtos.RegisterUserDTO;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.responses.LoginResponse;
import irusri.assignment.todo_app.services.AuthenticationService;
import irusri.assignment.todo_app.services.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    // Endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterUserDTO registerUserDto) {
        logger.info("Request to register user: {}", registerUserDto.getEmail());
        User registeredUser = authenticationService.registerUser(registerUserDto);
        logger.info("User registered successfully with ID: {}", registeredUser.getId());
        return ResponseEntity.ok(registeredUser);
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginUserDTO loginUserDto) {
        logger.info("Request to login user: {}", loginUserDto.getEmail());
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
        logger.info("User logged in successfully with ID: {}", authenticatedUser.getId());
        return ResponseEntity.ok(loginResponse);
    }
}