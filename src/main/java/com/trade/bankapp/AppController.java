package com.trade.bankapp;

import com.trade.bankapp.marketdata.MarketDataExchangeOne;
import com.trade.bankapp.marketdata.MarketDataExchangeTwo;
import com.trade.bankapp.marketdata.MarketService;
import com.trade.bankapp.order.Order;
import com.trade.bankapp.portfolio.Portfolio;
import com.trade.bankapp.users.Client;
import com.trade.bankapp.users.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppController {

    @Autowired
    private final ClientService clientService;

    @Autowired
    private final MarketService marketService;

    @GetMapping
    public String viewHomePage() {
        return "index";
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("client", new Client());
         return "signUp";
    }


    @PostMapping("/register-process")
    public String processRegistration(@RequestBody Client client) {
        clientService.saveClientToRepo(client);
        return "successfulReg";
    }

    @GetMapping("/login/client")
    public String clientDashBoard(){
        return "clientDashboard";
    }


    @GetMapping("/login/client/create-portfolio")
    public String createPortfolio() {
        return "createPortfolio";
    }


    @PostMapping("/login/client/create-portfolio/portfolio")
    public String portfolio(@RequestBody Portfolio portfolio) {
        clientService.saveClientPortfolio(portfolio);
        return "portfolioCreated";
    }

    @GetMapping("/login/client/portfolio-list")
    public String listPortfolio(Model model) {
        List<Portfolio> listPortfolios = clientService.getAllClientPortfolio();
        model.addAttribute("listPortfolios", listPortfolios);
        return "portfolioList";
    }

    @GetMapping("/login/client/delete-portfolio")
    public String portfolioDeleted() {
        return "deletePortfolio";
    }


    @DeleteMapping("/login/client/delete-portfolio/{id}")
    @ResponseBody
    public String deletePortfolio(@PathVariable("id") Long portfolioId ){
        clientService.deletePortfolioById(portfolioId);
        return "Portfolio Deleted";
    }

    @GetMapping("/login/client/update-portfolio")
    public String portfolioUpdated() {
        return "updatePortfolio";
    }


    @PutMapping("/login/client/update-portfolio/{id}")
    @ResponseBody
    public String updatePortfolio(@PathVariable("id") Long portfolioId, @RequestBody Portfolio portfolio) {
        clientService.updatePortfolio(portfolioId,portfolio);
        return "Portfolio updated";
    }

    @GetMapping("/login/client/create-order")
    public String createOrder() {
        return "createOrder";
    }


    @PostMapping("/login/client/create-order/order")
    public String order(@RequestBody Order order) {
        clientService.saveClientOrder(order);

        return "orderCreated";
    }

    @GetMapping("/login/client/order-list")
    public String listOrders(Model model) {
        List<Order> listOrders = clientService.getAllClientOrders();
        model.addAttribute("listOrders", listOrders);
        return "orderList";
    }

    @GetMapping("/login/client/check-status/{id}")
    @ResponseBody
    public ResponseEntity<String> orderStatus(@PathVariable("id") Long orderId ) {

        return clientService.checkOrderStatus(orderId);
    }

    @GetMapping("/login/client/delete-order")
    public String orderDeleted() {
        return "deleteOrder";
    }


    @DeleteMapping("/login/client/delete-order/{id}")
    @ResponseBody
    public String deleteOrder(@PathVariable("id") Long orderId ){

        clientService.deleteOrderById(orderId);
        return "Order deleted";
    }


    @GetMapping("/login/client/update-order")
    public String orderUpdated() {
        return "updateOrder";
    }


    @PutMapping("/login/client/update-order/{id}")
    public void updateOrder(@PathVariable("id") Long orderId, @RequestBody Order order) {
        clientService.updateOrder(orderId,order);
    }

//    @PostMapping("/api/md2")
//    @ResponseBody
//    public String getMarketDataExchangeTwo(@RequestBody List<MarketDataExchangeTwo> marketData){
//        marketService.saveExchangeTwoMarketData(marketData);
//        return "Thanks for the update";
//    }
//
//    @PostMapping("/api/md")
//    @ResponseBody
//    public String getMarketData(@RequestBody List<MarketDataExchangeOne> marketData){
//        marketService.saveExchangeOneMarketData(marketData);
//        return "Thanks for the update";
//    }


    @PostMapping("/api/buy")
    @ResponseBody
    public String makeOrderForBuy(@RequestBody Order body){

        marketService.decideWhichExchangeToTradeOnIfBuy(body);
        return "Buy Order done processing";

    }

    @PostMapping("/api/sell")
    @ResponseBody
    public String makeOrderForSell(@RequestBody Order body){

        marketService.decideWhichExchangeToTradeOnIfSell(body);
        return "Sell Order done processing";
    }

    @GetMapping("/api/bad-order")
    @ResponseBody
     public String badOrder(){
        return "Order is not reasonable. Check market data";
    }
}
