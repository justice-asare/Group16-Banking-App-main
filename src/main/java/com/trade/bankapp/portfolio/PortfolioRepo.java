package com.trade.bankapp.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepo extends JpaRepository<Portfolio,Long> {
    Portfolio findPortfolioByUsername(String username);

    Optional<Portfolio> findFirstByProductOrderByLocalDateTimeDescUsername(String username);

    Optional<Portfolio> findFirstByProductOrderByLocalDateTimeDesc(String product);

    @Query(nativeQuery = true, value = "select * from portfolio p " +
                                       "where p.username = :username and p.product = :product " +
                                       "order by local_date_time desc LIMIT 1")
    Optional<Portfolio> findPortfolio(@Param("username")String username,@Param("product")String product);
}
