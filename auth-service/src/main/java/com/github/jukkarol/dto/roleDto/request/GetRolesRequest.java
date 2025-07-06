package com.github.jukkarol.dto.roleDto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record GetRolesRequest(
        @JsonIgnore
        Long userId
) { }