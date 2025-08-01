package com.github.jukkarol.controller;

import com.github.jukkarol.dto.userDto.request.LoginUserRequest;
import com.github.jukkarol.dto.userDto.request.RegisterUserRequest;
import com.github.jukkarol.dto.userDto.response.LoginResponse;
import com.github.jukkarol.dto.userDto.response.RegisterUserResponse;
import com.github.jukkarol.model.User;
import com.github.jukkarol.service.AuthenticationService;
import com.github.jukkarol.service.JwtServiceExtended;
import com.github.jukkarol.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("auth")
@RestController
@Tag(name = "Authentication", description = "Authentication endpoints for basic user operations")
public class AuthController {
    private final JwtServiceExtended jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("signup")
    @Operation(
            summary = "User registration",
            description = "Creates a new user account and returns user details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterUserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request format or validation errors"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with this email already exists"
            )
    })
    public ResponseEntity<RegisterUserResponse> register(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        RegisterUserResponse registeredUser = authenticationService.signup(registerUserRequest);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("login")
    @Operation(
            summary = "User login",
            description = "Authenticates user with email and password, returns JWT token on success"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request format"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            )
    })
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginUserRequest loginUserRequest) {
        User authenticatedUser = authenticationService.authenticate(loginUserRequest);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        long jwtExpirationTime = jwtService.getExpirationTime();

        LoginResponse response = new LoginResponse(jwtToken, jwtExpirationTime);

        return ResponseEntity.ok(response);
    }
}