package net.chrotos.chrotoscloud.rest.services;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.games.stats.GameStatistic;
import net.chrotos.chrotoscloud.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/players/{uuid}/gamestats")
public class GameStatisticService extends PlayerFetchingService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<? extends GameStatistic> getGameStats(@PathParam("uuid") UUID uuid, @QueryParam("gamemode") String gameMode) {
        Player player = getPlayer(uuid);

        if (gameMode == null) {
            return new ArrayList<>(player.getStats());
        }

        return new ArrayList<>(player.getStats(gameMode));
    }
}
