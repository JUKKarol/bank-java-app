package com.github.jukkarol.dto.creditDto.event;

import java.util.List;

public record CreditRequestEvent(
        List<SingleCreditRequest> creditRequests
) { }
