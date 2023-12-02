package com.menes.dw.repository;

import com.menes.dw.model.CurrencyExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Integer> {
    @Query(value = "SELECT * FROM currency_exchange_rates WHERE publish_date = :date", nativeQuery = true)
    List<CurrencyExchange> findAllByPublishDate(LocalDate date);
}
