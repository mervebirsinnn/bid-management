package com.merve.bid;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bidderName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    protected Bid() {
        // JPA için gerekli
    }

    public Bid(String bidderName, BigDecimal amount) {
        this.bidderName = bidderName;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getBidderName() {
        return bidderName;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}