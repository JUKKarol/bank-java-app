package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.withdrawalDto.request.MakeWithdrawalRequest;
import com.github.jukkarol.dto.withdrawalDto.response.MakeWithdrawalResponse;
import com.github.jukkarol.model.Withdrawal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {
    Withdrawal makeWithdrawalRequestToWithdrawal(MakeWithdrawalRequest request);

    MakeWithdrawalResponse withdrawalToWithdrawalResponse(Withdrawal withdrawal);
}
