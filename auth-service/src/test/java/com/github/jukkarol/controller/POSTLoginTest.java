package com.github.jukkarol.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jukkarol.dto.userDto.request.LoginUserRequest;
import com.github.jukkarol.model.User;
import com.github.jukkarol.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class POSTLoginTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("test@example.com");
        user.setName("TestUser");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);
    }

    @Test
    void shouldReturn200WhenLoginIsSuccessful() throws Exception {
        LoginUserRequest loginRequest = new LoginUserRequest("test@example.com", "password123");

        mockMvc.perform(post("/auth//login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").exists());
    }

    @Test
    void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
        LoginUserRequest loginRequest = new LoginUserRequest("test@example.com", "wrongpassword");

        mockMvc.perform(post("/auth//login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        String invalidJson = """
                {
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/auth//login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}