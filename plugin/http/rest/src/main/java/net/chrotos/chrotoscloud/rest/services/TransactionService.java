package net.chrotos.chrotoscloud.rest.services;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players/{uuid}/accounts/{id}/transactions")
public class TransactionService extends PlayerFetchingService {
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transaction> getAccount(@PathParam("uuid") UUID uuid, @PathParam("id") UUID id) {
        Account account = getPlayer(uuid).getAccount(id);

        if (account == null) {
            throw new NotFoundException();
        }

        return new ArrayList<>(account.getTransactions());
    }
}
