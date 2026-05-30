package com.github.jukkarol.mapper;

import com.github.jukkarol.dto.creditDto.event.request.SingleCreditRequest;
import com.github.jukkarol.dto.creditHistoryDto.CreditHistoryDisplayDto;
import com.github.jukkarol.model.Credit;
import com.github.jukkarol.model.CreditHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditHistoryMapper {
    @Mapping(target = "credit", source = "credit")
    @Mapping(target = "amount", source = "credit.amountMonthly")
    @Mapping(target = "amountLeft", source = "amountLeft")
    @Mapping(target = "installmentLeft", expression = "java(credit.getInstallmentLeft() - 1)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CreditHistory creditToCreditHistory(Credit credit, BigDecimal amountLeft);

    List<CreditHistoryDisplayDto> creditHistoriesToCreditHistoriesDisplayDto(List<CreditHistory> creditHistories);
}
