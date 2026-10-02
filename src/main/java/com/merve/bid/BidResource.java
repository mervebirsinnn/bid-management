package com.merve.bid;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

@Path("/bids")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BidResource {

    @Inject
    private BidService bidService;

    public static class CreateBidRequest {
        public String bidderName;
        public BigDecimal amount;
    }

    @POST
    public Response create(CreateBidRequest request) {
        if (request == null
                || request.bidderName == null
                || request.bidderName.isBlank()
                || request.amount == null
                || request.amount.signum() <= 0
                || request.amount.scale() > 2
                || request.amount.compareTo(
                    new BigDecimal("99999999999999999.99")) > 0) {

            throw new BadRequestException(
                "Name and a positive amount with at most 2 decimals are required."
            );
        }

        Bid bid = bidService.create(
            request.bidderName.trim(),
            request.amount
        );

        return Response.status(Response.Status.CREATED)
                .entity(bid)
                .build();
    }

    @GET
    public List<Bid> list() {
        return bidService.findAll();
    }
}