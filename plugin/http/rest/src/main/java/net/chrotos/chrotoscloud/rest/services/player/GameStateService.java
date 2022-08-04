package net.chrotos.chrotoscloud.rest.services.player;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.games.states.GameState;
import net.chrotos.chrotoscloud.player.Player;
import net.chrotos.chrotoscloud.rest.middleware.Cache;
import net.chrotos.chrotoscloud.rest.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players/{uuid}/gamestates")
public class GameStateService extends PlayerFetchingService {
    @Cache(seconds = 180)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response<List<? extends GameState>> getGameStates(@PathParam("uuid") UUID uuid, @QueryParam("gamemode") String gameMode,
                                                            @QueryParam("name") String name) {
        Player player = getPlayer(uuid);

        if (name != null) {
            return new Response<>(new ArrayList<>(player.getStatesByName(name)));
        }

        if (gameMode != null) {
            return new Response<>(new ArrayList<>(player.getStates(gameMode)));
        }

        return new Response<>(new ArrayList<>(player.getStates()));
    }
}
