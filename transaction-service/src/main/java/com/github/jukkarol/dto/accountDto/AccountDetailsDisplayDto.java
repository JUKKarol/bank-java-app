package com.github.jukkarol.dto.accountDto;

import java.time.LocalDateTime;

public record AccountDetailsDisplayDto(
         Integer balance,

         String accountNumber,

         Long userId,

         LocalDateTime createdAt,

         LocalDateTime updatedAt
) { }