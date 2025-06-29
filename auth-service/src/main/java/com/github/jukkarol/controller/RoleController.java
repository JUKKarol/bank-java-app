package com.github.jukkarol.controller;

import com.github.jukkarol.dto.roleDto.request.CreateRoleRequest;
import com.github.jukkarol.dto.roleDto.request.DeleteRoleRequest;
import com.github.jukkarol.dto.roleDto.response.CreateRoleResponse;
import com.github.jukkarol.dto.roleDto.response.DeleteRoleResponse;
import com.github.jukkarol.dto.userDto.request.LoginUserRequest;
import com.github.jukkarol.dto.userDto.request.RegisterUserRequest;
import com.github.jukkarol.dto.userDto.response.LoginResponse;
import com.github.jukkarol.dto.userDto.response.RegisterUserResponse;
import com.github.jukkarol.model.User;
import com.github.jukkarol.service.AuthenticationService;
import com.github.jukkarol.service.JwtServiceExtended;
import com.github.jukkarol.service.RoleService;
import com.github.jukkarol.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("role")
@RestController
@Tag(name = "Role", description = "Role endpoints for basic user operations")
public class RoleController {
    private final JwtServiceExtended jwtService;
    private final AuthenticationService authenticationService;
    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Add user Role",
            description = "Create new role for specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role created successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateRoleResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request format"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied"
            )
    })
    public ResponseEntity<CreateRoleResponse> createRole(@RequestBody @Valid CreateRoleRequest request) {
        CreateRoleResponse response = roleService.createUserRole(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    @Operation(
            summary = "Delete user Role",
            description = "Delete role for specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role created successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeleteRoleResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request format"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permission denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role not found"
            )
    })
    public ResponseEntity<DeleteRoleResponse> deleteRole(@RequestBody @Valid DeleteRoleRequest request) {
        DeleteRoleResponse response = roleService.deleteUserRole(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}