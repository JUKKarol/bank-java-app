package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.userDto.DisplayUserDto;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<DisplayUserDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(userService.findUserById(currentUser.getId()));
    }

    @GetMapping("/")
    public ResponseEntity<List<DisplayUserDto>> allUsers() {
        List<DisplayUserDto> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }
}
