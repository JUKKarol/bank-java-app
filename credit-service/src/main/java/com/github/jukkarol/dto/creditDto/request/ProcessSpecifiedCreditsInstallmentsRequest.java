package com.github.jukkarol.dto.creditDto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProcessSpecifiedCreditsInstallmentsRequest (
        @NotNull
        List<Long> ids
) {}
