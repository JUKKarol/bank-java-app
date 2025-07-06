package com.github.jukkarol.service;

import com.github.jukkarol.dto.roleDto.request.CreateRoleRequest;
import com.github.jukkarol.dto.roleDto.request.DeleteRoleRequest;
import com.github.jukkarol.dto.roleDto.request.GetRolesRequest;
import com.github.jukkarol.dto.roleDto.response.CreateRoleResponse;
import com.github.jukkarol.dto.roleDto.response.DeleteRoleResponse;
import com.github.jukkarol.dto.roleDto.response.GetRolesResponse;
import com.github.jukkarol.exception.NotFoundException;
import com.github.jukkarol.model.User;
import com.github.jukkarol.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@AllArgsConstructor
@Service
public class RoleService {
    private final UserRepository userRepository;

    public CreateRoleResponse createUserRole(CreateRoleRequest request){
        User user = userRepository.findByEmail(request.userEmail())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), request.userEmail()));

        String normalizedRole = standardize(request.role());

        if(!user.getRoles().contains(normalizedRole))
        {
            user.getRoles().add(normalizedRole);
            userRepository.save(user);
        }

        return new CreateRoleResponse(user.getId(), user.getRoles());
    }

    public DeleteRoleResponse deleteUserRole(DeleteRoleRequest request){
        User user = userRepository.findByEmail(request.userEmail())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), request.userEmail()));

        String normalizedRole = standardize(request.role());

        Set<String> roles = user.getRoles();

        if (!roles.remove(normalizedRole)) {
            throw new NotFoundException("Role", normalizedRole);
        }

        userRepository.save(user);

        return new DeleteRoleResponse(user.getId(), roles);
    }

    public GetRolesResponse getUserRoles(GetRolesRequest request){
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), request.userId().toString()));

        return new GetRolesResponse(user.getId(), user.getRoles());
    }

    private String standardize(String raw) {
        String upper = raw.trim().toUpperCase(Locale.ROOT);
        return upper.startsWith("ROLE_") ? upper
                : "ROLE_" + upper;
    }
}

