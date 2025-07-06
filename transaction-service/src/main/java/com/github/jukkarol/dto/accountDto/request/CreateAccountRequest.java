package com.github.jukkarol.dto.accountDto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record CreateAccountRequest(
        @JsonIgnore
        Long userId
) { }