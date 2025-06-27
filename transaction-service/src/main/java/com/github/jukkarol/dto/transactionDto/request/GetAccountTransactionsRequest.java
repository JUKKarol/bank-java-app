package com.github.jukkarol.dto.transactionDto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAccountTransactionsRequest {
    @JsonIgnore
    private Long userId;

    @Size(min=10, max=10)
    @NotEmpty
    private String accountNumber;
}
