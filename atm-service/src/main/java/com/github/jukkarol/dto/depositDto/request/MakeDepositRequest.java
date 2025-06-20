package com.github.jukkarol.dto.depositDto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeDepositRequest {
    @NotNull
    @Positive
    private Integer amount;

    @Size(min=10, max=10)
    @NotEmpty
    private String accountNumber;
}
