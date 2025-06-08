package com.github.jukkarol.dto.transactionDto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class MakeTransactionRequest {
    @JsonIgnore
    private Long userId;

    @Size(min=10, max=10)
    @NotEmpty
    private String fromAccountNumber;
    @NotEmpty
    @Size(min=10, max=10)
    private String toAccountNumber;

    @NotNull
    @Positive
    private Integer amount;
}
