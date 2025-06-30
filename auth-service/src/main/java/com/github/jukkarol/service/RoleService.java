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

@AllArgsConstructor
@Service
public class RoleService {
    private final UserRepository userRepository;

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

    public DeleteRoleResponse deleteUserRole(DeleteRoleRequest request){
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), request.getUserEmail()));

        request.setRole(request.getRole().toUpperCase());
        if(!request.getRole().startsWith("ROLE_")){
            request.setRole("ROLE_" + request.getRole());
        }

        List<String> userRoles = user.getRoles();

        if(userRoles.contains(request.getRole()))
        {
            userRoles.remove(request.getRole());
            user.setRoles(userRoles);
            userRepository.save(user);
        }
        else
        {
            throw new NotFoundException("Role", request.getRole());
        }

        return new DeleteRoleResponse(user.getId(), user.getRoles());
    }

    public GetRolesResponse getUserRoles(GetRolesRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), request.getUserId().toString()));

        return new GetRolesResponse(user.getId(), user.getRoles());
    }
}

