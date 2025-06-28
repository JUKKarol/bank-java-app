package com.github.jukkarol.repository;

import com.github.jukkarol.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByFromAccountNumberOrToAccountNumber(
            String fromAccountNumber, String toAccountNumber);
}
