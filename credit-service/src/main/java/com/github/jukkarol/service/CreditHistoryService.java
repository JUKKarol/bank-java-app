package com.github.jukkarol.service;

import com.github.jukkarol.dto.creditDto.response.GetAccountCreditsResponse;
import com.github.jukkarol.dto.creditHistoryDto.CreditHistoryDisplayDto;
import com.github.jukkarol.dto.creditHistoryDto.response.GetCreditHistoriesResponse;
import com.github.jukkarol.mapper.CreditHistoryMapper;
import com.github.jukkarol.model.CreditHistory;
import com.github.jukkarol.repository.CreditHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CreditHistoryService {
    private final CreditHistoryRepository creditHistoryRepository;
    private final CreditHistoryMapper creditHistoryMapper;

    public GetCreditHistoriesResponse getCreditsHistoriesByCreditId(Long creditId)
    {
        List<CreditHistory> creditHistories = creditHistoryRepository.findAllByCreditId(creditId);

        List<CreditHistoryDisplayDto> creditsDto = creditHistoryMapper.creditHistoriesToCreditHistoriesDisplayDto(creditHistories);

        return new GetCreditHistoriesResponse(creditsDto);
    }
}
