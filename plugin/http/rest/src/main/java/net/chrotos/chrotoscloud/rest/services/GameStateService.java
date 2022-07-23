package net.chrotos.chrotoscloud.rest.services;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.games.states.GameState;
import net.chrotos.chrotoscloud.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players/{uuid}/gamestates")
public class GameStateService extends PlayerFetchingService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<? extends GameState> getGameStates(@PathParam("uuid") UUID uuid, @QueryParam("gamemode") String gameMode) {
        Player player = getPlayer(uuid);

        if (gameMode == null) {
            return new ArrayList<>(player.getStates());
        }

        return new ArrayList<>(player.getStates(gameMode));
    }
}
