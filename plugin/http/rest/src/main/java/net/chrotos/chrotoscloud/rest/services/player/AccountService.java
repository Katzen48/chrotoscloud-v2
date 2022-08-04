package net.chrotos.chrotoscloud.rest.services.player;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.economy.Account;
import net.chrotos.chrotoscloud.economy.AccountType;
import net.chrotos.chrotoscloud.rest.middleware.Cache;
import net.chrotos.chrotoscloud.rest.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players/{uuid}/accounts")
public class AccountService extends PlayerFetchingService {
    @Cache(seconds = 180)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response<List<Account>> getAccounts(@PathParam("uuid") UUID uuid, @QueryParam("type") AccountType type) {
        if (type == null) {
            return new Response<>(new ArrayList<>(getPlayer(uuid).getAccounts()));
        }

        return new Response<>(new ArrayList<>(getPlayer(uuid).getAccounts(type)));
    }

    @Cache(seconds = 60)
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response<Account> getAccount(@PathParam("uuid") UUID uuid, @PathParam("id") UUID id) {
        return new Response<>(getPlayer(uuid).getAccount(id));
    }
}
