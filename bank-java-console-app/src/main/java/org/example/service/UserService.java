package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.userDto.DisplayUserDto;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public List<DisplayUserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<DisplayUserDto> usersDto = users.stream()
                .map(user -> new DisplayUserDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getBalance(),
                        user.getAccountNumber(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                ))
                .toList();

        return usersDto;
    }
}
