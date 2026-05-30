package jukkarol.service;

import com.github.jukkarol.dto.creditDto.event.request.CreditRequestEvent;
import com.github.jukkarol.dto.creditDto.event.request.SingleCreditRequest;
import com.github.jukkarol.dto.creditDto.request.CreateCreditRequest;
import com.github.jukkarol.dto.creditDto.response.CreateCreditResponse;
import com.github.jukkarol.exception.NotFoundException;
import com.github.jukkarol.mapper.CreditHistoryMapper;
import com.github.jukkarol.mapper.CreditMapper;
import com.github.jukkarol.model.Credit;
import com.github.jukkarol.model.CreditHistory;
import com.github.jukkarol.repository.CreditHistoryRepository;
import com.github.jukkarol.repository.CreditRepository;
import com.github.jukkarol.service.CreditService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private CreditHistoryRepository creditHistoryRepository;

    @Mock
    private CreditMapper creditMapper;

    @Mock
    private CreditHistoryMapper creditHistoryMapper;

    @Mock
    private KafkaTemplate<String, CreditRequestEvent> kafkaTemplate;

    @InjectMocks
    private CreditService creditService;

    @Test
    void shouldCreateCreditAndProcessInstallments() throws BadRequestException {
        // given
        CreateCreditRequest request = new CreateCreditRequest(
                new BigDecimal("1000"),
                new BigDecimal("100"),
                "1234567890"
        );

        Credit credit = new Credit();
        credit.setInstallmentTotal(10);
        credit.setInstallmentLeft(10);

        CreateCreditResponse expectedResponse = new CreateCreditResponse(
                1L,
                new BigDecimal("1000"),
                new BigDecimal("100"),
                10,
                10,
                "1234567890",
                LocalDateTime.of(2024, 1, 1, 12, 0)
        );

        when(creditMapper.createCreditRequestToCredit(request)).thenReturn(credit);
        when(creditMapper.creditToCreateCreditResponse(credit)).thenReturn(expectedResponse);

        // when
        CreateCreditResponse result = creditService.createCredit(request);

        // then
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(creditRepository).save(credit);
    }

    @Test
    void shouldProcessCreditsInstallments() {
        // given
        Credit credit = Credit.builder()
                .id(1L)
                .amountTotal(new BigDecimal(1000))
                .amountMonthly(new BigDecimal(100))
                .installmentTotal(5).installmentLeft(3)
                .accountNumber("1234567890")
                .build();

        CreditHistory history = new CreditHistory();
        List<Credit> credits = List.of(credit);
        List<SingleCreditRequest> creditsDto = List.of(new SingleCreditRequest(new BigDecimal(1000), "1234567890"));

        when(creditRepository.findAllCreditsToDecrementInstallments()).thenReturn(credits);
        when(creditHistoryMapper.creditToCreditHistory(eq(credit), any(BigDecimal.class))).thenReturn(history);
        when(creditRepository.decrementInstallmentsForAll()).thenReturn(1);
        when(creditMapper.creditsToSingleCreditsRequests(credits)).thenReturn(creditsDto);

        // when
        creditService.processCreditsInstallments();

        // then
        verify(creditHistoryRepository).saveAll(List.of(history));
        verify(creditRepository).decrementInstallmentsForAll();
        verify(kafkaTemplate).send(eq("credit-requests"), any(CreditRequestEvent.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNoCreditsToProcess() {
        // given
        when(creditRepository.findAllCreditsToDecrementInstallments()).thenReturn(Collections.emptyList());

        // then
        assertThrows(NotFoundException.class, () -> creditService.processCreditsInstallments());
    }
}