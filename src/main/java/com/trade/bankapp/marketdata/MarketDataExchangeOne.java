package com.trade.bankapp.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@Table(name = "market_exchange_one")
@AllArgsConstructor
@NoArgsConstructor
public class MarketDataExchangeOne {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @JsonProperty("TICKER")
    private String ticker;
    @JsonProperty("SELL_LIMIT")
    private Integer sellLimit;
    @JsonProperty("LAST_TRADED_PRICE")
    private Double lastTradedPrice;
    @JsonProperty("MAX_PRICE_SHIFT")
    private Double maxPriceShift;
    @JsonProperty("ASK_PRICE")
    private Double askPrice;
    @JsonProperty("BID_PRICE")
    private Double bidPrice;
    @JsonProperty("BUY_LIMIT")
    private Integer buyLimit;

    private LocalDateTime localDateTime = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketDataExchangeOne that = (MarketDataExchangeOne) o;
        return ticker.equals(that.ticker) && sellLimit.equals(that.sellLimit) && lastTradedPrice.equals(that.lastTradedPrice) && maxPriceShift.equals(that.maxPriceShift) && askPrice.equals(that.askPrice) && bidPrice.equals(that.bidPrice) && buyLimit.equals(that.buyLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, sellLimit, lastTradedPrice, maxPriceShift, askPrice, bidPrice, buyLimit);
    }
}
