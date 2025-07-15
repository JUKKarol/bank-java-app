package com.github.jukkarol.controller;

import com.github.jukkarol.configs.JwtAuthenticationToken;
import com.github.jukkarol.dto.roleDto.request.CreateRoleRequest;
import com.github.jukkarol.dto.roleDto.request.DeleteRoleRequest;
import com.github.jukkarol.dto.roleDto.request.GetRolesRequest;
import com.github.jukkarol.dto.roleDto.response.CreateRoleResponse;
import com.github.jukkarol.dto.roleDto.response.DeleteRoleResponse;
import com.github.jukkarol.dto.roleDto.response.GetRolesResponse;
import com.github.jukkarol.exception.PermissionDeniedException;
import com.github.jukkarol.service.AuthenticationService;
import com.github.jukkarol.service.JwtServiceExtended;
import com.github.jukkarol.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("roles")
@RestController
@Tag(name = "Role", description = "Role endpoints for basic user operations")
public class RoleController {
    private final JwtServiceExtended jwtService;
    private final AuthenticationService authenticationService;
    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Add user role",
            description = "Create new role for specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
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

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    @Operation(
            summary = "Delete user role",
            description = "Delete role for specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role deleted successful",
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

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Get user roles",
            description = "Get all roles for logged user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role returned successful",
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
            )
    })
    public ResponseEntity<GetRolesResponse> getRoles(@RequestBody @Valid GetRolesRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Long userId = jwtAuth.getUserId();

            GetRolesRequest processedRequest = new GetRolesRequest(userId);
            GetRolesResponse response = roleService.getUserRoles(processedRequest);

            return ResponseEntity.ok(response);
        }

        throw new PermissionDeniedException();
    }
}