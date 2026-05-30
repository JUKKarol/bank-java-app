package com.github.jukkarol.dto.creditHistoryDto.response;

import com.github.jukkarol.dto.creditHistoryDto.CreditHistoryDisplayDto;

import java.util.List;

public record GetCreditHistoriesResponse(
        List<CreditHistoryDisplayDto> credits
){ }
