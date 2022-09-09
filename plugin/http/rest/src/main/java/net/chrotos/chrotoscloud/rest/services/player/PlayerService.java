package net.chrotos.chrotoscloud.rest.services.player;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.player.CloudPlayer;
import net.chrotos.chrotoscloud.player.Player;
import net.chrotos.chrotoscloud.rest.middleware.Cache;
import net.chrotos.chrotoscloud.rest.middleware.authentication.Authenticate;
import net.chrotos.chrotoscloud.rest.response.PagedResponse;
import net.chrotos.chrotoscloud.rest.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players")
public class PlayerService extends PlayerFetchingService {
    @Authenticate
    @Cache(seconds = 60)
    @GET
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response<Player> getPlayerById(@PathParam("uuid") UUID uuid) {
            return new Response<>(getPlayer(uuid));
    }

    @Authenticate
    @Cache(seconds = 600)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PagedResponse<? extends Player> getPlayers(@QueryParam("limit") int limit, @QueryParam("first") int first) {
        int pageSize = 25;
        int from = Math.max(0, first);

        if (limit > 0) {
            pageSize = Math.min(limit, 1000);
        }
        DataSelectFilter filter = DataSelectFilter.builder()
                .first(from)
                .pageSize(pageSize)
                .build();

        return new PagedResponse<>(getPlayers(filter), first, pageSize);
    }

    @Authenticate
    @DELETE
    @Path("{uuid}")
    @Produces(MediaType.TEXT_PLAIN)
    public void deletePlayer(@PathParam("uuid") UUID uuid) {
        Player player = getPlayer(uuid);

        player.kick();
        Cloud.getInstance().getPersistence().delete(player);
    }

    private List<Player> getPlayers(DataSelectFilter filter) {
        return new ArrayList<>(Cloud.getInstance().getPersistence().getAll(CloudPlayer.class, filter));
    }
}
