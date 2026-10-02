package com.merve.bid;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.math.BigDecimal;
import java.util.List;

@Named
@RequestScoped
public class BidView {

    @Inject
    private BidService bidService;

    private String bidderName;
    private BigDecimal amount;

    public void save() {
        if (amount == null || amount.signum() <= 0
                || amount.scale() > 2
                || amount.compareTo(
                    new BigDecimal("99999999999999999.99")) > 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid amount", "Use a positive amount with at most 2 decimals."));
            return;
        }

        bidService.create(bidderName.trim(), amount);
        bidderName = null;
        amount = null;

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage("Bid saved"));
    }

    public List<Bid> getBids() {
        return bidService.findAll();
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}