package net.chrotos.chrotoscloud.rest.services.player;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.player.Ban;
import net.chrotos.chrotoscloud.rest.middleware.Cache;
import net.chrotos.chrotoscloud.rest.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players/{uuid}/bans")
public class BansService extends PlayerFetchingService {
    @Cache(seconds = 180)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response<List<Ban>> getBans(@PathParam("uuid") UUID uuid) {
        return new Response<>(new ArrayList<>(getPlayer(uuid).getBans()));
    }
}
