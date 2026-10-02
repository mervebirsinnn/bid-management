package com.merve.bid;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class BidService {

    @PersistenceContext(unitName = "bidPU")
    private EntityManager em;

    @Inject
    private Event<BidCreated> bidCreated;

    @Transactional
    public Bid create(String bidderName, BigDecimal amount) {
        Bid bid = new Bid(bidderName, amount);
        em.persist(bid);
        em.flush();//commit değil dbye gönderir henüz geri alınabilir

        bidCreated.fire(new BidCreated(bid.getId()));

        return bid;
    }

    @Transactional
    public List<Bid> findAll() {
        return em.createQuery(
            "select b from Bid b order by b.id desc",
            Bid.class
        ).getResultList();
    }
}