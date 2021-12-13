package com.trade.bankapp.users;

import com.trade.bankapp.marketdata.*;
import com.trade.bankapp.order.Order;
import com.trade.bankapp.order.OrderRepo;
import com.trade.bankapp.portfolio.Portfolio;
import com.trade.bankapp.portfolio.PortfolioRepo;
import com.trade.bankapp.subscriber.Receiver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {
    @Autowired
    ClientRepo clientRepo;

    @Autowired
    PortfolioRepo portfolioRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    ExchangeTwoMarketRepo exchangeTwoMarketRepo;

    @Autowired
    ExchangeOneMarketRepo exchangeOneMarketRepo;



    public void saveClientToRepo(Client client){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(client.getPassword());
        client.setPassword(encodedPassword);
        clientRepo.save(client);
    }

    public void saveClientPortfolio(Portfolio portfolio)
    {

        Optional<MarketDataExchangeOne> exchangeOne =
                exchangeOneMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(portfolio.getProduct());
        Optional<MarketDataExchangeTwo> exchangeTwo =
                exchangeTwoMarketRepo.findFirstByTickerOrderByLocalDateTimeDesc(portfolio.getProduct());

        if (exchangeOne.isPresent() && exchangeTwo.isPresent()) {
            Double valueFromExOne = exchangeOne.get().getAskPrice() * portfolio.getQuantity();

            Double valueFromExTwo = exchangeTwo.get().getAskPrice() * portfolio.getQuantity();

            portfolio.setPortfolioValue((valueFromExOne * valueFromExTwo)/2);

            updatePortfolioRepo(portfolio);

        }
    }

    public List<Portfolio> getAllClientPortfolio(){
        return portfolioRepo.findAll();
    }

    public void deletePortfolioById(Long id){
        portfolioRepo.deleteById(id);
    }

    public void updatePortfolio(Long id, Portfolio portfolio)
    {
        Portfolio portfolio1 = portfolioRepo.getById(id);
        portfolio1.setId(id);
        portfolio1.setQuantity(portfolio.getQuantity());
        portfolio1.setProduct(portfolio.getProduct());
        portfolioRepo.save(portfolio1);
    }

    public List<Order> getAllClientOrders(){
        return orderRepo.findAll();
    }

    public void deleteOrderById(Long id)
    {
        Order order = orderRepo.getById(id);
        URI fullAddressUri = getUri(order);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(fullAddressUri);
        orderRepo.deleteById(id);
    }



    public void updateOrder(Long id, Order order)
    {
        Order fetchedFromRepo = orderRepo.getById(id);
        URI fullAddress = getUri(fetchedFromRepo);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Order> request = new HttpEntity<>(order, headers);

        restTemplate.put(fullAddress,request);

        update(id, order, fetchedFromRepo);
        orderRepo.save(fetchedFromRepo);
    }

    public ResponseEntity<String> checkOrderStatus(Long id)
    {
        Order order = orderRepo.getById(id);
        URI fullAddress = getUri(order);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.getForEntity(fullAddress,String.class);
        return entity;

    }

    public void saveClientOrder(Order order) {
        orderRepo.save(order);
    }

    private URI getUri(Order order)
    {
        String token = order.getToken();
        String uri = order.getUri();
        String fullAddress = uri+token;
        URI fullAddressUri = URI.create(fullAddress);
        return fullAddressUri;
    }

    private void update(Long id, Order order, Order fetchedFromRepo)
    {
        fetchedFromRepo.setId(id);
        fetchedFromRepo.setQuantity(order.getQuantity());
        fetchedFromRepo.setProduct(order.getProduct());
        fetchedFromRepo.setPrice(order.getPrice());
        fetchedFromRepo.setSide(order.getSide());
        fetchedFromRepo.setUsername(order.getUsername());
    }

    public void updatePortfolioRepo(Portfolio portfolio)
    {
        Optional<Portfolio> portfolio1 =
                portfolioRepo.findFirstByProductOrderByLocalDateTimeDesc(portfolio.getProduct());
        if ((portfolio1 == null) || (portfolio1.isPresent() && !portfolio1.get().equals(portfolio))) {
            portfolioRepo.save(portfolio);
        }
    }

    public void updateClient(Client client){
        Client client1 = new Client();
        client1.setPassword(client.getPassword());
        client1.setEmail(client.getEmail());
        client1.setLastname(client.getLastname());
        client1.setFirstname(client.getFirstname());
        client1.setId(client.getId());
        client1.setAccountBal(client.getAccountBal());
        clientRepo.delete(client);
        clientRepo.save(client1);
    }
}
