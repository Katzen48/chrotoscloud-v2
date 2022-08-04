package net.chrotos.chrotoscloud.rest.services.player;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.player.PlayerInventory;
import net.chrotos.chrotoscloud.rest.middleware.Cache;
import net.chrotos.chrotoscloud.rest.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players/{uuid}/inventories")
public class InventoryService extends PlayerFetchingService {
    @Cache(seconds = 180)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response<List<? extends PlayerInventory>> getInventories(@PathParam("uuid") UUID uuid) {
        return new Response<>(new ArrayList<>(getPlayer(uuid).getInventories()));
    }

    @Cache(seconds = 60)
    @GET
    @Path("{gamemode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response<PlayerInventory> getInventory(@PathParam("uuid") UUID uuid, @PathParam("gamemode") String gameMode) {
        return new Response<>(getPlayer(uuid).getInventory(gameMode));
    }
}
