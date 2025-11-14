package com.github.jukkarol.dto.creditDto.event.request;

import java.util.List;

public record CreditRequestEvent(
        List<SingleCreditRequest> creditRequests
) { }
