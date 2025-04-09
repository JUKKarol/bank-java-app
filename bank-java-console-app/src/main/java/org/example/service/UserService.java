package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.userDto.CreateUserDto;
import org.example.dto.userDto.DisplayUserDto;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Integer getUserBalance(String accountNumber) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        return user.map(User::getBalance).orElse(0);

    }

    public boolean loginUser(String accountNumber, String password) {
        Optional<User> user = userRepository.findByAccountNumber(accountNumber);

        return user.map(value -> value.getPassword().equals(password)).orElse(false);
    }

    public User addUser(CreateUserDto userDto) {
        User user = new User(null, userDto.getName(), userDto.getEmail(), userDto.getPassword(), userDto.getBalance(), userDto.getAccountNumber(), null, null);
        return userRepository.save(user);
    }

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
