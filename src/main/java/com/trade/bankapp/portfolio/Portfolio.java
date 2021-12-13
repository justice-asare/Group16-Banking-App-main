package com.trade.bankapp.portfolio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@Table(name = "portfolio")
@AllArgsConstructor
@NoArgsConstructor
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @JsonProperty("product")
    private String product;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("username")
    private String username;

    private Double portfolioValue;
    private LocalDateTime localDateTime = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return product.equals(portfolio.product) &&
                quantity.equals(portfolio.quantity) && username.equals(portfolio.username)
                && portfolioValue.equals(portfolio.portfolioValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, username, portfolioValue, localDateTime);
    }
}
