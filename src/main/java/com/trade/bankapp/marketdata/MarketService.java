package com.trade.bankapp.marketdata;

import com.trade.bankapp.exceptions.InvalidOrder;
import com.trade.bankapp.exceptions.OrderNotReasonableException;
import com.trade.bankapp.order.Order;
import com.trade.bankapp.order.OrderRepo;
import com.trade.bankapp.portfolio.Portfolio;
import com.trade.bankapp.portfolio.PortfolioRepo;
import com.trade.bankapp.subscriber.Receiver;
import com.trade.bankapp.users.Client;
import com.trade.bankapp.users.ClientRepo;
import com.trade.bankapp.users.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {
    @Autowired
    ExchangeOneMarketRepo exchangeOneMarketRepo;

    @Autowired
    ExchangeTwoMarketRepo exchangeTwoMarketRepo;

    @Autowired
    OrderRepo orderRepo;

    public MarketService(OrderRepo orderRepo){
        this.orderRepo = orderRepo ;
    }

    public MarketService(PortfolioRepo portfolioRepo){
        this.portfolioRepo = portfolioRepo ;
    }

    @Autowired
    ClientRepo clientRepo;

    @Autowired
    PortfolioRepo portfolioRepo;


    public void saveExchangeOneMarketData(List<MarketDataExchangeOne> marketData) {
        marketData.forEach((MarketDataExchangeOne tickerData) -> {
            Optional<MarketDataExchangeOne> ticker =
                    exchangeOneMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(tickerData.getTicker());
            if ( ticker == null || (ticker.isPresent() && !ticker.get().equals(tickerData))) {
                exchangeOneMarketRepo.save(tickerData);
            }
        });

    }

    public void saveExchangeTwoMarketData(List<MarketDataExchangeTwo> marketData) {
        marketData.forEach((MarketDataExchangeTwo tickerData) -> {
            Optional<MarketDataExchangeTwo> ticker =
                    exchangeTwoMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(tickerData.getTicker());
            if (ticker == null || (ticker.isPresent() && !ticker.get().equals(tickerData))) {
                exchangeTwoMarketRepo.save(tickerData);
            }
        });

    }

    private String uri1 = "https://exchange.matraining.com/70ea673e-577f-45ac-9f0f-929b702f61d4/order/";
    private String uri2 = "https://exchange2.matraining.com/70ea673e-577f-45ac-9f0f-929b702f61d4/order/";

    public void decideWhichExchangeToTradeOnIfSell(Order order) {
        saveExchangeOneMarketData(Receiver.exchangeOneMarketData);
        saveExchangeTwoMarketData(Receiver.exchangeTwoMarketData);

        validateSaleOrderQuantity(order);
        verifyIfSellOrderIsReasonable(order);

        Optional<MarketDataExchangeOne> exchangeOneMarketData =
                exchangeOneMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(order.getProduct());
        Optional<MarketDataExchangeTwo> exchangeTwoMarketData =
                exchangeTwoMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(order.getProduct());


        if (exchangeOneMarketData.isPresent() && exchangeTwoMarketData.isPresent()) {
            Double exchangeOneBidPrice = exchangeOneMarketData.get().getBidPrice();
            Double exchangeTwoBidPrice = exchangeTwoMarketData.get().getBidPrice();

            comparePrices(order, exchangeOneBidPrice, exchangeTwoBidPrice, uri1, uri2);
        }
    }




    public void decideWhichExchangeToTradeOnIfBuy(Order order) {
        saveExchangeOneMarketData(Receiver.exchangeOneMarketData);
        saveExchangeTwoMarketData(Receiver.exchangeTwoMarketData);

        validateBuyOrderAmount(order);

        Optional<MarketDataExchangeOne> exchangeOneMarketData =
                exchangeOneMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(order.getProduct());
        Optional<MarketDataExchangeTwo> exchangeTwoMarketData =
                exchangeTwoMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(order.getProduct());

        if (exchangeOneMarketData.isPresent() && exchangeTwoMarketData.isPresent()) {
            Double exchangeOneAskPrice = exchangeOneMarketData.get().getAskPrice();
            Double exchangeTwoAskPrice = exchangeTwoMarketData.get().getAskPrice();

            comparePrices(order, exchangeOneAskPrice, exchangeTwoAskPrice, uri1, uri2);

        }
    }





    protected void comparePrices(Order order, Double orderPriceExcOne,
                               Double orderPriceExcTwo, String s, String s2)
    {
        String side = order.getSide();

        switch (side.toUpperCase()) {
            case "BUY" : {
                if (orderPriceExcOne < orderPriceExcTwo) {
                    order.setUri(s);
                    sendOrderToExchange(order);
                } else {
                    order.setUri(s2);
                    sendOrderToExchange(order);
                }
            } break;
            case "SELL" :{
                if (orderPriceExcOne > orderPriceExcTwo) {
                    order.setUri(s);
                    sendOrderToExchange(order);
                } else {
                    order.setUri(s2);
                    sendOrderToExchange(order);
                }
            }
            }
        }


    public String sendOrderToExchange(Order body) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Order> request = new HttpEntity<>(body, headers);
        String token = restTemplate.postForObject(body.getUri(), request, String.class);
        body.setToken(removeQuotes(token));
        orderRepo.save(body);
        return token;
    }


    public String removeQuotes(String uri) {
        String hold = uri.substring(1, uri.length() - 1);
        return hold;
    }

    @Autowired
    ClientService clientService;

    public Portfolio validateSaleOrderQuantity(Order order){
     Optional<Portfolio> portfolio = portfolioRepo.findPortfolio(order.getUsername(),order.getProduct());
     if(portfolio.isPresent()){
     Integer compareQuantity = portfolio.get().getQuantity() - order.getQuantity() ;
                if(compareQuantity >= 0){
                   portfolio.get().setQuantity(compareQuantity);
                   clientService.saveClientPortfolio(portfolio.get());
                } else {
                    throw new InvalidOrder("You don't own enough stocks");
                }
     } else{
         throw new InvalidOrder("You don't own any stocks");
     }
     return portfolio.get();
    }





    public Client validateBuyOrderAmount(Order order) {
        Client client = clientRepo.findByEmail(order.getUsername());

            Double orderValue = order.getPrice() * order.getQuantity();


            Double finalValue = client.getAccountBal() - orderValue;


            if (finalValue >= 0) {

                client.setAccountBal(finalValue);
                clientService.updateClient(client);

            } else {

                throw new InvalidOrder("Check Account Balance");

            }

        return client ;
        }


    public void verifyIfSellOrderIsReasonable(Order order){
        Optional<MarketDataExchangeOne> exchangeOneMarketData =
                exchangeOneMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(order.getProduct());
        Optional<MarketDataExchangeTwo> exchangeTwoMarketData =
                exchangeTwoMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(order.getProduct());

        if (exchangeOneMarketData.isPresent() && exchangeTwoMarketData.isPresent()) {
            Double exchangeOneAskPrice = exchangeOneMarketData.get().getAskPrice();
            Double exchangeTwoAskPrice = exchangeTwoMarketData.get().getAskPrice();

            if((order.getPrice() > exchangeOneAskPrice) || (order.getPrice() > exchangeTwoAskPrice)){
                throw new OrderNotReasonableException("Order is not reasonable. Check Market data");
            }
        }
    }
}