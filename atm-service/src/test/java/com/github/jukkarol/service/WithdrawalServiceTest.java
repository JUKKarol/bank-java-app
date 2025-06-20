package com.github.jukkarol.service;

import com.github.jukkarol.dto.withdrawalDto.event.WithdrawalRequestedEvent;
import com.github.jukkarol.dto.withdrawalDto.request.MakeWithdrawalRequest;
import com.github.jukkarol.dto.withdrawalDto.response.MakeWithdrawalResponse;
import com.github.jukkarol.mapper.WithdrawalMapper;
import com.github.jukkarol.model.Withdrawal;
import com.github.jukkarol.repository.WithdrawalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WithdrawalServiceTest {

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @Mock
    private WithdrawalMapper withdrawalMapper;

    @Mock
    private KafkaTemplate<String, WithdrawalRequestedEvent> kafkaTemplate;

    @InjectMocks
    private WithdrawalService withdrawalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void makeWithdrawal_shouldSaveWithdrawalAndSendKafkaEvent() {
        // given
        MakeWithdrawalRequest request = new MakeWithdrawalRequest();
        request.setAccountNumber("789012");
        request.setAmount(200);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAccountNumber("789012");
        withdrawal.setAmount(200);

        MakeWithdrawalResponse response = new MakeWithdrawalResponse();
        response.setAccountNumber("789012");
        response.setAmount(200);

        when(withdrawalMapper.makeWithdrawalRequestToWithdrawal(request)).thenReturn(withdrawal);
        when(withdrawalMapper.withdrawalToWithdrawalResponse(withdrawal)).thenReturn(response);

        // when
        MakeWithdrawalResponse actualResponse = withdrawalService.makeWithdrawal(request);

        // then
        verify(withdrawalMapper).makeWithdrawalRequestToWithdrawal(request);
        verify(kafkaTemplate).send(eq("withdrawal-requests"), argThat(event ->
                event.getAccountNumber().equals("789012") && event.getAmount().equals(200)
        ));
        verify(withdrawalRepository).save(withdrawal);
        verify(withdrawalMapper).withdrawalToWithdrawalResponse(withdrawal);

        assertNotNull(actualResponse);
        assertEquals("789012", actualResponse.getAccountNumber());
        assertEquals(200, actualResponse.getAmount());
    }

    @Test
    void requestWithdrawal_shouldSendKafkaEvent() {
        // given
        String accountNumber = "456789";
        Integer amount = 300;

        // when
        withdrawalService.requestWithdrawal(accountNumber, amount);

        // then
        verify(kafkaTemplate).send(eq("withdrawal-requests"), argThat(event ->
                event.getAccountNumber().equals(accountNumber) && event.getAmount().equals(amount)
        ));
    }
}
