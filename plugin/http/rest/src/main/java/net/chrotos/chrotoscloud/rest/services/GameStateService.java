package net.chrotos.chrotoscloud.rest.services;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.games.states.CloudGameState;
import net.chrotos.chrotoscloud.games.states.GameState;
import net.chrotos.chrotoscloud.rest.middleware.Cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/gamestates")
public class GameStateService {
    @Cache(seconds = 600)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<? extends GameState> getGameStates(@QueryParam("gamemode") String gameMode,
                                                   @QueryParam("name") String name) {
        if (name != null) {
            return new ArrayList<>(Cloud.getInstance().getPersistence()
                    .getFiltered(CloudGameState.class, "name", Collections.singletonMap("name", name)));
        }

        if (gameMode != null) {
            return new ArrayList<>(Cloud.getInstance().getPersistence()
                    .getFiltered(CloudGameState.class, "gameMode", Collections.singletonMap("gameMode", gameMode)));
        }

        throw new BadRequestException("Either \"gamemode\" or \"name\" query parameter has to be set");
    }
}
