package com.trade.bankapp.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "client_order")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @JsonProperty("product")
    private String product;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("side")
    private String side;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("username")
    private String username;

    private String token;
    private String uri;

}
