package net.chrotos.chrotoscloud.rest.services;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.games.states.CloudGameState;
import net.chrotos.chrotoscloud.games.states.GameState;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;
import net.chrotos.chrotoscloud.rest.exception.BadRequestException;
import net.chrotos.chrotoscloud.rest.middleware.Cache;
import net.chrotos.chrotoscloud.rest.response.PagedResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("/gamestates")
public class GameStateService {
    @Cache(seconds = 600)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PagedResponse<? extends GameState> getGameStates(@QueryParam("gamemode") String gameMode,
                                                            @QueryParam("name") String name,
                                                            @QueryParam("limit") int limit,
                                                            @QueryParam("first") int first) {
        int pageSize = 25;
        int from = Math.max(0, first);

        if (limit > 0) {
            pageSize = Math.min(limit, 1000);
        }
        DataSelectFilter filter = DataSelectFilter.builder()
                                                  .first(from)
                                                  .pageSize(pageSize)
                                                  .build();

        List<? extends GameState> data;

        if (name != null) {
            data = new ArrayList<>(Cloud.getInstance().getPersistence()
                    .getFiltered(CloudGameState.class, "name", Collections.singletonMap("name", name), filter));
        } else if (gameMode != null) {
            data = new ArrayList<>(Cloud.getInstance().getPersistence()
                    .getFiltered(CloudGameState.class, "gameMode", Collections.singletonMap("gameMode", gameMode),
                            filter));
        } else {
            throw new BadRequestException("Either \"gamemode\" or \"name\" query parameter has to be set");
        }

        return new PagedResponse<>(data, first, pageSize);
    }
}
