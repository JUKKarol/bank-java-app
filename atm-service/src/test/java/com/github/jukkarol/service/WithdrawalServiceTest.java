package com.github.jukkarol.service;

import com.github.jukkarol.dto.withdrawalDto.event.request.WithdrawalRequestEvent;
import com.github.jukkarol.dto.withdrawalDto.event.response.WithdrawalResponseEvent;
import com.github.jukkarol.dto.withdrawalDto.request.MakeWithdrawalRequest;
import com.github.jukkarol.dto.withdrawalDto.response.MakeWithdrawalResponse;
import com.github.jukkarol.exception.InsufficientFundsException;
import com.github.jukkarol.exception.ServiceUnavailableException;
import com.github.jukkarol.exception.SystemException;
import com.github.jukkarol.mapper.WithdrawalMapper;
import com.github.jukkarol.model.Withdrawal;
import com.github.jukkarol.model.enums.WithdrawalStatus;
import com.github.jukkarol.repository.WithdrawalRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WithdrawalServiceTest {

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @Mock
    private WithdrawalMapper withdrawalMapper;

    @Mock
    private ReplyingKafkaTemplate<String, WithdrawalRequestEvent, WithdrawalResponseEvent> replyingKafkaTemplate;

    @Mock
    private RequestReplyFuture<String, WithdrawalRequestEvent, WithdrawalResponseEvent> requestReplyFuture;

    @Mock
    private ConsumerRecord<String, WithdrawalResponseEvent> consumerRecord;

    @InjectMocks
    private WithdrawalService withdrawalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void makeWithdrawal_shouldSaveWithdrawalAndSendKafkaEvent() throws Exception {
        // given
        MakeWithdrawalRequest request = new MakeWithdrawalRequest(100, "1234567890");

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAccountNumber("1234567890");
        withdrawal.setAmount(100);

        MakeWithdrawalResponse expectedResponse = new MakeWithdrawalResponse(
                100, "1234567890"
        );

        WithdrawalResponseEvent successResponse = new WithdrawalResponseEvent(
                "transaction-id", true, "Success", 400, 123342L
        );

        when(withdrawalMapper.makeWithdrawalRequestToWithdrawal(request)).thenReturn(withdrawal);
        when(withdrawalRepository.save(any(Withdrawal.class))).thenReturn(withdrawal);
        when(replyingKafkaTemplate.sendAndReceive(any(ProducerRecord.class))).thenReturn(requestReplyFuture);
        when(requestReplyFuture.get(anyLong(), any())).thenReturn(consumerRecord);
        when(consumerRecord.value()).thenReturn(successResponse);
        when(withdrawalMapper.withdrawalToWithdrawalResponse(any(Withdrawal.class))).thenReturn(expectedResponse);

        // when
        MakeWithdrawalResponse actualResponse = withdrawalService.makeWithdrawal(request);

        // then
        verify(withdrawalMapper).makeWithdrawalRequestToWithdrawal(request);
        verify(withdrawalRepository, times(2)).save(any(Withdrawal.class));
        verify(replyingKafkaTemplate).sendAndReceive(any(ProducerRecord.class));
        verify(withdrawalMapper).withdrawalToWithdrawalResponse(any(Withdrawal.class));

        assertNotNull(actualResponse);
        assertEquals("1234567890", actualResponse.accountNumber());
        assertEquals(100, actualResponse.amount());
    }

    @Test
    void makeWithdrawal_InsufficientFunds_shouldThrowException() throws Exception {
        // given
        MakeWithdrawalRequest request = new MakeWithdrawalRequest(100, "1234567890");

        Withdrawal withdrawal = new Withdrawal();
        WithdrawalResponseEvent failureResponse = new WithdrawalResponseEvent(
                "transaction-id", false, "Insufficient funds", null, null
        );

        when(withdrawalMapper.makeWithdrawalRequestToWithdrawal(request)).thenReturn(withdrawal);
        when(withdrawalRepository.save(any(Withdrawal.class))).thenReturn(withdrawal);
        when(replyingKafkaTemplate.sendAndReceive(any(ProducerRecord.class))).thenReturn(requestReplyFuture);
        when(requestReplyFuture.get(anyLong(), any())).thenReturn(consumerRecord);
        when(consumerRecord.value()).thenReturn(failureResponse);

        // when & then
        assertThrows(InsufficientFundsException.class, () -> withdrawalService.makeWithdrawal(request));
        verify(withdrawalRepository, times(2)).save(any(Withdrawal.class));
    }

    @Test
    void makeWithdrawal_TimeoutException_shouldThrowServiceUnavailable() throws Exception {
        // given
        MakeWithdrawalRequest request = new MakeWithdrawalRequest(100, "1234567890");

        Withdrawal withdrawal = new Withdrawal();

        when(withdrawalMapper.makeWithdrawalRequestToWithdrawal(request)).thenReturn(withdrawal);
        when(withdrawalRepository.save(any(Withdrawal.class))).thenReturn(withdrawal);
        when(replyingKafkaTemplate.sendAndReceive(any(ProducerRecord.class))).thenReturn(requestReplyFuture);
        when(requestReplyFuture.get(anyLong(), any())).thenThrow(new TimeoutException());

        // when & then
        assertThrows(ServiceUnavailableException.class, () -> withdrawalService.makeWithdrawal(request));
        verify(withdrawalRepository, times(2)).save(any(Withdrawal.class));
    }

    @Test
    void getWithdrawalStatus_shouldReturnStatus() {
        // given
        String transactionId = "test-transaction-id";
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setStatus(WithdrawalStatus.COMPLETED);

        when(withdrawalRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(withdrawal));

        // when
        WithdrawalStatus result = withdrawalService.getWithdrawalStatus(transactionId);

        // then
        assertEquals(WithdrawalStatus.COMPLETED, result);
        verify(withdrawalRepository).findByTransactionId(transactionId);
    }

    @Test
    void getWithdrawalStatus_TransactionNotFound_shouldThrowException() {
        // given
        String transactionId = "non-existent";
        when(withdrawalRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> withdrawalService.getWithdrawalStatus(transactionId));
    }
}