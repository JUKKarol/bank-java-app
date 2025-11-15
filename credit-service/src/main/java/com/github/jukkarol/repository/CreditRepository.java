package com.github.jukkarol.repository;

import com.github.jukkarol.model.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Credit c SET c.installmentLeft = c.installmentLeft -1 WHERE c.installmentLeft > 0")
    int decrementInstallmentsForAll();

    @Query("SELECT * FROM Credit WHERE installmentLeft > 0")
    List<Credit> findAllCreditsToDecrementInstallments();
}
