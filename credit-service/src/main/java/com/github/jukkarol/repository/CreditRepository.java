package com.github.jukkarol.repository;

import com.github.jukkarol.model.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Credit c SET c.installmentLeft = c.installmentLeft -1 WHERE c.installmentLeft > 0")
    int decrementInstallmentsForAll();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Credit c SET c.installmentLeft = c.installmentLeft -1 WHERE c.installmentLeft > 0 AND c.id IN :ids")
    int decrementSpecifiedInstallments(List<Long> ids);

    @Query("SELECT c FROM Credit c WHERE c.installmentLeft > 0")
    List<Credit> findAllCreditsToDecrementInstallments();

    @Query("SELECT c FROM Credit c WHERE c.installmentLeft > 0 AND c.id IN :ids")
    List<Credit> findSpecifiedCreditsToDecrementInstallments(List<Long> ids);

    List<Credit> findAllCreditsByAccountNumber(String accountNumber);
}
