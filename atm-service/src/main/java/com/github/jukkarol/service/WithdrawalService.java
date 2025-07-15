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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@AllArgsConstructor
@Service
public class WithdrawalService {
    private final WithdrawalRepository withdrawalRepository;
    private final WithdrawalMapper withdrawalMapper;
    private final ReplyingKafkaTemplate<String, WithdrawalRequestEvent, WithdrawalResponseEvent> replyingKafkaTemplate;

    @Transactional
    public MakeWithdrawalResponse makeWithdrawal(MakeWithdrawalRequest request) {
        Withdrawal withdrawal = withdrawalMapper.makeWithdrawalRequestToWithdrawal(request);
        withdrawal.setTransactionId(UUID.randomUUID().toString());
        withdrawal.setStatus(WithdrawalStatus.PENDING);

        Withdrawal savedWithdrawal = withdrawalRepository.save(withdrawal);

        WithdrawalRequestEvent requestEvent = new WithdrawalRequestEvent(
                savedWithdrawal.getTransactionId(),
                savedWithdrawal.getAccountNumber(),
                savedWithdrawal.getAmount()
        );

        try {
            log.info("Sending withdrawal request for transaction: {}", savedWithdrawal.getTransactionId());

            RequestReplyFuture<String, WithdrawalRequestEvent, WithdrawalResponseEvent> future =
                    replyingKafkaTemplate.sendAndReceive(
                            new ProducerRecord<>("withdrawal-requests", requestEvent.transactionId(), requestEvent)
                    );

            WithdrawalResponseEvent response = future.get(10, TimeUnit.SECONDS).value();

            if (response.success()) {
                savedWithdrawal.setStatus(WithdrawalStatus.COMPLETED);
                savedWithdrawal.setFinalBalance(response.remainingBalance());
                savedWithdrawal.setTransactionDbId(response.transactionDbId());

                log.info("Withdrawal completed successfully for transaction: {}", savedWithdrawal.getTransactionId());
            } else {
                savedWithdrawal.setStatus(WithdrawalStatus.REJECTED);
                savedWithdrawal.setRejectionReason(response.message());

                log.warn("Withdrawal rejected for transaction: {}, reason: {}",
                        savedWithdrawal.getTransactionId(), response.message());

                if (response.message().contains("Insufficient funds")) {
                    throw new InsufficientFundsException("Insufficient funds for withdrawal");
                } else if (response.message().contains("Account not found")) {
                    throw new IllegalArgumentException("Account not found: " + request.accountNumber());
                } else {
                    throw new SystemException("Withdrawal failed: " + response.message());
                }
            }

            withdrawalRepository.save(savedWithdrawal);
            return withdrawalMapper.withdrawalToWithdrawalResponse(savedWithdrawal);
        } catch (TimeoutException e) {
            log.error("Timeout waiting for withdrawal response for transaction: {}", savedWithdrawal.getTransactionId());
            savedWithdrawal.setStatus(WithdrawalStatus.TIMEOUT);
            savedWithdrawal.setRejectionReason("Service timeout");
            withdrawalRepository.save(savedWithdrawal);
            throw new ServiceUnavailableException("Withdrawal service temporarily unavailable");

        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for withdrawal response for transaction: {}", savedWithdrawal.getTransactionId());
            Thread.currentThread().interrupt(); // Restore interrupted status
            savedWithdrawal.setStatus(WithdrawalStatus.FAILED);
            savedWithdrawal.setRejectionReason("Operation interrupted");
            withdrawalRepository.save(savedWithdrawal);
            throw new ServiceUnavailableException("Withdrawal service temporarily unavailable");

        } catch (ExecutionException e) {
            log.error("Execution error while processing withdrawal for transaction: {}", savedWithdrawal.getTransactionId(), e);
            savedWithdrawal.setStatus(WithdrawalStatus.FAILED);
            savedWithdrawal.setRejectionReason("Communication error: " + e.getCause().getMessage());
            withdrawalRepository.save(savedWithdrawal);
            throw new SystemException("Withdrawal processing failed due to communication error");

        } catch (Exception e) {
            log.error("Error processing withdrawal for transaction: {}", savedWithdrawal.getTransactionId(), e);
            savedWithdrawal.setStatus(WithdrawalStatus.FAILED);
            savedWithdrawal.setRejectionReason("System error: " + e.getMessage());
            withdrawalRepository.save(savedWithdrawal);

            if (e instanceof InsufficientFundsException || e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new SystemException("Withdrawal processing failed");
        }
    }

    public WithdrawalStatus getWithdrawalStatus(String transactionId) {
        return withdrawalRepository.findByTransactionId(transactionId)
                .map(Withdrawal::getStatus)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
    }
}
