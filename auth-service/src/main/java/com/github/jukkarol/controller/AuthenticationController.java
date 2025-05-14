package com.github.jukkarol.controller;

import com.github.jukkarol.dto.userDto.request.LoginUserRequest;
import com.github.jukkarol.dto.userDto.request.RegisterUserRequest;
import com.github.jukkarol.dto.userDto.response.LoginResponse;
import com.github.jukkarol.dto.userDto.response.RegisterUserResponse;
import com.github.jukkarol.service.AuthenticationService;
import com.github.jukkarol.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest registerUserRequest) {
        RegisterUserResponse registeredUser = authenticationService.signup(registerUserRequest);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserRequest loginUserRequest) {
        UserDetails authenticatedUser = authenticationService.authenticate(loginUserRequest);

        String jwtToken = jwtService.generateToken((UserDetails) authenticatedUser);
        long jwtExpirationTime = jwtService.getExpirationTime();

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtExpirationTime);

        return ResponseEntity.ok(loginResponse);
    }
}