package com.github.jukkarol.dto.roleDto.response;

import java.util.Set;

public record GetRolesResponse(
        Long userId,

        Set<String> roles
) { }