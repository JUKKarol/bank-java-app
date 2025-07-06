package com.github.jukkarol.dto.roleDto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record DeleteRoleRequest(
        @NotEmpty
        @Email
        String userEmail,

        @NotEmpty
        @Length(min=2, max=16)
        String role
) { }
