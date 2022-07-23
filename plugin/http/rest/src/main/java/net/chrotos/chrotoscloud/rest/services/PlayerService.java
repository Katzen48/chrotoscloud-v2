package net.chrotos.chrotoscloud.rest.services;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.player.Player;

import java.util.UUID;

@Path("/players")
public class PlayerService extends PlayerFetchingService {
    @GET
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Player getPlayer(@PathParam("uuid") UUID uuid) {
        return super.getPlayer(uuid);
    }
}
