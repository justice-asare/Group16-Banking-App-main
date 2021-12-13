package com.trade.bankapp.marketdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeTwoMarketRepo extends JpaRepository<MarketDataExchangeTwo,Long> {
    Optional<MarketDataExchangeTwo> findFirstByTickerOrderByLocalDateTimeDesc(String ticker);
}
