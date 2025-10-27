package com.github.jukkarol.dto.accountDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDetailsDisplayDto(
        BigDecimal balance,

         String accountNumber,

         Long userId,

         LocalDateTime createdAt,

         LocalDateTime updatedAt
) { }