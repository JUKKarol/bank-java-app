package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.userDto.DisplayUserDto;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<DisplayUserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return userMapper.usersToDisplayUserDtos(users);
    }
}
