package com.github.jukkarol.service;

import com.github.jukkarol.dto.depositDto.request.MakeDepositRequest;
import com.github.jukkarol.dto.depositDto.response.MakeDepositResponse;
import com.github.jukkarol.mapper.DepositMapper;
import com.github.jukkarol.model.Deposit;
import com.github.jukkarol.repository.DepositRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DepositService {
    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;

    public MakeDepositResponse makeDeposit(MakeDepositRequest request)
    {
        Deposit deposit = depositMapper.makeDepositRequestToDeposit(request);
//        deposit.setBalanceAfterTransaction( + deposit.getAmount());

        depositRepository.save(deposit);

        return depositMapper.depositToMakeDepositResponse(deposit);
    }
}
