package com.github.jukkarol.dto.roleDto.response;

import java.util.Set;

public record DeleteRoleResponse(
        Long userId,

        Set<String> roles
) { }