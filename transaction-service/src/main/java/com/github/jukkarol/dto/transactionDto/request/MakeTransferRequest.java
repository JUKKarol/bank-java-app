package com.github.jukkarol.dto.transactionDto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeTransferRequest {
    @JsonIgnore
    private Long userId;

    private String fromAccountNumber;
    private String toAccountNumber;

    private Integer amount;
}
