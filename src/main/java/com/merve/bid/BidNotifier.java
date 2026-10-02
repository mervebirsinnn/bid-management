package com.merve.bid;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.faces.push.Push;
import jakarta.faces.push.PushContext;
import jakarta.inject.Inject;

@ApplicationScoped
public class BidNotifier {

    @Inject
    @Push(channel = "bids")
    private PushContext push;

    @Resource(lookup = "java:comp/DefaultManagedExecutorService")
    private ManagedExecutorService executor;

    public void afterCommit(
        @Observes(during = TransactionPhase.AFTER_SUCCESS)//observer, transaction başarılı olduktan sonra çalışır
        BidCreated event
    ) {
        executor.execute(() -> push.send("BID_CREATED"));//bildirim işini sunucunun yönettiği başka bir threade verir
    }
}