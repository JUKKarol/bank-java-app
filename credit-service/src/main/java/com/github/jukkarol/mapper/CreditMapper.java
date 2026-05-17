package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.creditDto.event.request.SingleCreditRequest;
import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.model.Credit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit createCreditRequestToCredit(CreateCreditRequest request);

    @Mapping(target = "amount", source = "amountMonthly") // ← tu, na single
    SingleCreditRequest creditToSingleCreditRequest(Credit credit);

    List<SingleCreditRequest> creditsToSingleCreditsRequests(List<Credit> credits); // MapStruct sam użyje metody wyżej

    CreateCreditResponse creditToCreateCreditResponse(Credit credit);
}
