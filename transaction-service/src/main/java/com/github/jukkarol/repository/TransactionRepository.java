package com.github.jukkarol.repository;

import com.github.jukkarol.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAllByFromAccountNumberOrToAccountNumber(
            String fromAccountNumber, String toAccountNumber, Pageable pageable);
    Optional<Transaction> findByTransactionId(String transactionId);
}
