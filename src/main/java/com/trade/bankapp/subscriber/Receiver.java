package com.trade.bankapp.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bankapp.marketdata.MarketDataExchangeOne;
import com.trade.bankapp.marketdata.MarketDataExchangeTwo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class Receiver {

    public static List<MarketDataExchangeOne> exchangeOneMarketData = new ArrayList<>();
    public static List<MarketDataExchangeTwo> exchangeTwoMarketData = new ArrayList<>();
    Logger logger = LoggerFactory.getLogger(Receiver.class);
    ObjectMapper objectMapper = new ObjectMapper();

    public void marketDataFromExchangeOne(String message) throws JsonProcessingException {
        MarketDataExchangeOne[] marketData = objectMapper.readValue(message, MarketDataExchangeOne[].class);
        exchangeOneMarketData = Arrays.asList(marketData);
        logger.info("Consumed message from exchange one{}", exchangeOneMarketData);
    }


    public void marketDataFromExchangeTwo(String message) throws JsonProcessingException {
        MarketDataExchangeTwo[] marketData = objectMapper.readValue(message, MarketDataExchangeTwo[].class);
        exchangeTwoMarketData = Arrays.asList(marketData);
        logger.info("Consumed message from exchange two {}", exchangeTwoMarketData);
    }
}
