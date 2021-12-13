package com.trade.bankapp.marketdata;

import com.trade.bankapp.order.Order;
import com.trade.bankapp.order.OrderRepo;
import com.trade.bankapp.portfolio.Portfolio;
import com.trade.bankapp.portfolio.PortfolioRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

class MarketServiceTest {
    MarketService marketService = new MarketService(Mockito.mock(OrderRepo.class));
    MarketService marketService2 = new MarketService(Mockito.mock(PortfolioRepo.class));
    @Test
    public void testForComparePrices(){
        //MarketDataExchangeTwo marketDataExchangeTwo = new MarketDataExchangeTwo();
        Double valueOfExchangeOne = 2.0;
        Double valueOfExchangeTwo = 1.0;

        Order order = new Order(1L,"TSLA",50,"BUY",
                1.0,"did@done","abdyeh","gwtsv");
        String uri1 = "https://exchange2.matraining.com/70ea673e-577f-45ac-9f0f-929b702f61d4/order";
        String uri2 = "https://exchange.matraining.com/70ea673e-577f-45ac-9f0f-929b702f61d4/order/";

        marketService.comparePrices(order,valueOfExchangeOne,valueOfExchangeTwo,uri1,uri2);
        String actual = uri2;
        String expected = order.getUri();
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void testForComparePricesIfSell(){

        Double valueOfExchangeOne = 2.0;
        Double valueOfExchangeTwo = 1.0;

        Order order = new Order(1L,"TSLA",50,"SELL",
                1.0,"did@done","abdyeh","gwtsv");
        String uri1 = "https://exchange2.matraining.com/70ea673e-577f-45ac-9f0f-929b702f61d4/order";
        String uri2 = "https://exchange.matraining.com/70ea673e-577f-45ac-9f0f-929b702f61d4/order/";

        marketService.comparePrices(order,valueOfExchangeOne,valueOfExchangeTwo,uri1,uri2);
        String actual = uri1;
        String expected = order.getUri();
        Assertions.assertEquals(expected,actual);
    }

//    @Test
//    public void testIfClientOwnsEnoughStock(){
//        Order order = new Order(1L,"TSLA",50,"SELL",
//                1.0,"did@done","abdyeh","gwtsv");
//
//        LocalDateTime localDateTime = LocalDateTime.now();
//      //  Portfolio portfolio = new Portfolio(1L,"IBM",60,"did@done",2777.0,localDateTime);
//
//        Portfolio expected = new Portfolio(1L,"IBM",10,"did@done",2777.0,localDateTime);
//        Portfolio actual = marketService2.validateSaleOrderQuantity(order);
//
//        Assertions.assertEquals(actual,expected);
//    }
}