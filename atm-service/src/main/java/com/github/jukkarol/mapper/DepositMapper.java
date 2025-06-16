package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.depositDto.request.MakeDepositRequest;
import com.github.jukkarol.dto.depositDto.response.MakeDepositResponse;
import com.github.jukkarol.model.Deposit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepositMapper {
    Deposit  makeDepositRequestToDeposit(MakeDepositRequest request);

    MakeDepositResponse depositToMakeDepositResponse(Deposit deposit);
}
