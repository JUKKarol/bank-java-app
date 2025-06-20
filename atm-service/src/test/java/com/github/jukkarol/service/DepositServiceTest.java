package com.github.jukkarol.service;

import com.github.jukkarol.dto.depositDto.event.DepositRequestedEvent;
import com.github.jukkarol.dto.depositDto.request.MakeDepositRequest;
import com.github.jukkarol.dto.depositDto.response.MakeDepositResponse;
import com.github.jukkarol.mapper.DepositMapper;
import com.github.jukkarol.model.Deposit;
import com.github.jukkarol.repository.DepositRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepositServiceTest {

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private DepositMapper depositMapper;

    @Mock
    private KafkaTemplate<String, DepositRequestedEvent> kafkaTemplate;

    @InjectMocks
    private DepositService depositService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void makeDeposit_shouldSaveDepositAndSendKafkaEvent() {
        // given
        MakeDepositRequest request = new MakeDepositRequest();
        request.setAccountNumber("123456");
        request.setAmount(100);

        Deposit deposit = new Deposit();
        deposit.setAccountNumber("123456");
        deposit.setAmount(100);

        MakeDepositResponse response = new MakeDepositResponse();
        response.setAccountNumber("123456");
        response.setAmount(100);

        when(depositMapper.makeDepositRequestToDeposit(request)).thenReturn(deposit);
        when(depositMapper.depositToMakeDepositResponse(deposit)).thenReturn(response);

        // when
        MakeDepositResponse actualResponse = depositService.makeDeposit(request);

        // then
        verify(depositMapper).makeDepositRequestToDeposit(request);
        verify(kafkaTemplate).send(eq("deposit-requests"), argThat(event ->
                event.getAccountNumber().equals("123456") && event.getAmount().equals(100)
        ));
        verify(depositRepository).save(deposit);
        verify(depositMapper).depositToMakeDepositResponse(deposit);

        assertNotNull(actualResponse);
        assertEquals("123456", actualResponse.getAccountNumber());
        assertEquals(100, actualResponse.getAmount());
    }

    @Test
    void requestDeposit_shouldSendKafkaEvent() {
        // given
        String accountNumber = "654321";
        Integer amount = 500;

        // when
        depositService.requestDeposit(accountNumber, amount);

        // then
        verify(kafkaTemplate).send(eq("deposit-requests"), argThat(event ->
                event.getAccountNumber().equals(accountNumber) && event.getAmount().equals(amount)
        ));
    }
}
