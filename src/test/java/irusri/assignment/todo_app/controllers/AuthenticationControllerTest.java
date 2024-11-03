package irusri.assignment.todo_app.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import irusri.assignment.todo_app.dtos.LoginUserDTO;
import irusri.assignment.todo_app.dtos.RegisterUserDTO;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.services.AuthenticationService;
import irusri.assignment.todo_app.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    // Test case for a successful signup
    @Test
    void testSignupSuccess() throws Exception {
        RegisterUserDTO registerUserDTO = new RegisterUserDTO("user@example.com", "password", "First", "Last");
        User savedUser = new User().setEmail("user@example.com").setFirstName("First").setLastName("Last");

        when(authenticationService.registerUser(any(RegisterUserDTO.class))).thenReturn(savedUser);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.firstName").value("First"))
                .andExpect(jsonPath("$.lastName").value("Last"));
    }

    // Test case for a successful login
    @Test
    void testLoginSuccess() throws Exception {
        LoginUserDTO loginUserDTO = new LoginUserDTO("user@example.com", "password");
        User authenticatedUser = new User().setEmail("user@example.com").setFirstName("First").setLastName("Last");

        when(authenticationService.authenticate(any(LoginUserDTO.class))).thenReturn(authenticatedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600L));
    }
}

