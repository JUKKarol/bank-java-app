package com.github.jukkarol.repository;

import com.github.jukkarol.model.CreditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {
    List<CreditHistory> findAllByCreditId(Long creditId);
}
