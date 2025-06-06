package com.github.jukkarol.dto.accountDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsDisplayDto {
    private Integer balance;

    private String accountNumber;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
