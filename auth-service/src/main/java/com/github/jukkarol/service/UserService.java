package com.github.jukkarol.service;

import com.github.jukkarol.dto.userDto.DisplayUserDto;
import com.github.jukkarol.mapper.UserMapper;
import com.github.jukkarol.model.User;
import com.github.jukkarol.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<DisplayUserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return userMapper.usersToDisplayUserDtos(users);
    }

    public DisplayUserDto findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        return userMapper.userToDisplayUserDto(user.orElse(null));
    }
}

