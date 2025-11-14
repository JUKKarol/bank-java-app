package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.creditDto.event.request.SingleCreditRequest;
import com.github.jukkarol.model.CreditHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditHistoryMapper {
    List<CreditHistory> listSingleCreditRequestToListCreditHistory(List<SingleCreditRequest> creditRequests);
}
