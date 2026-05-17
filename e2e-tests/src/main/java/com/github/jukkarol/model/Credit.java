package com.github.jukkarol.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Credit {
    private Long id;

    private BigDecimal amountTotal;

    private BigDecimal amountMonthly;

    private int installmentTotal;

    private int installmentLeft;

    private String accountNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
