package com.github.jukkarol.dto.depositDto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeDepositRequest {
    @JsonIgnore
    private Long userId;

    private Integer amount;

    private String accountNumber;
}
