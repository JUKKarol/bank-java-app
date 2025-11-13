package com.github.jukkarol.repository;

import com.github.jukkarol.model.CreditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {

}
