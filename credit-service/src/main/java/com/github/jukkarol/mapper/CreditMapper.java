package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.model.Credit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit createCreditRequestToCredit(CreateCreditRequest request);

    CreateCreditResponse creditToCreateCreditResponse(Credit credit);
}
