package net.chrotos.chrotoscloud.rest.services.player;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.games.states.GameState;
import net.chrotos.chrotoscloud.player.Player;
import net.chrotos.chrotoscloud.rest.middleware.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players/{uuid}")
public class GameStateService extends PlayerFetchingService {
    @Cache(seconds = 180)
    @GET
    @Path("/gamestates")
    @Produces(MediaType.APPLICATION_JSON)
    public List<? extends GameState> getGameStates(@PathParam("uuid") UUID uuid, @QueryParam("gamemode") String gameMode,
                                                   @QueryParam("name") String name) {
        Player player = getPlayer(uuid);

        if (name != null) {
            return new ArrayList<>(player.getStatesByName(name));
        }

        if (gameMode != null) {
            return new ArrayList<>(player.getStates(gameMode));
        }

        return new ArrayList<>(player.getStates());
    }
}
