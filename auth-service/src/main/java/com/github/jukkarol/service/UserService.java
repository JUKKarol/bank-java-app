package com.github.jukkarol.service;

import com.github.jukkarol.dto.roleDto.request.CreateRoleRequest;
import com.github.jukkarol.dto.roleDto.response.CreateRoleResponse;
import com.github.jukkarol.dto.userDto.DisplayUserDto;
import com.github.jukkarol.exception.NotFoundException;
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

    public CreateRoleResponse createUserRole(CreateRoleRequest request){
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), request.getUserEmail()));

        request.setRole(request.getRole().toUpperCase());
        if(!request.getRole().startsWith("ROLE_")){
            request.setRole("ROLE_" + request.getRole());
        }

        List<String> userRoles = user.getRoles();

        if(!userRoles.contains(request.getRole()))
        {
            userRoles.add(request.getRole());
            user.setRoles(userRoles);
            userRepository.save(user);
        }

        return new CreateRoleResponse(user.getId(), user.getRoles());
    }
}

