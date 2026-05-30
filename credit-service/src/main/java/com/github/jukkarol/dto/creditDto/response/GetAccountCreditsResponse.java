package com.github.jukkarol.dto.creditDto.response;

import com.github.jukkarol.dto.creditDto.CreditDisplayDto;

import java.util.List;

public record GetAccountCreditsResponse (
        List<CreditDisplayDto> credits
){ }
