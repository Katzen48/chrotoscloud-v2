package net.chrotos.chrotoscloud.paper.games;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.games.*;
import net.chrotos.chrotoscloud.games.events.*;
import net.chrotos.chrotoscloud.messaging.queue.Listener;
import net.chrotos.chrotoscloud.messaging.queue.Message;
import net.chrotos.chrotoscloud.messaging.queue.Registration;
import net.chrotos.chrotoscloud.paper.PaperCloud;
import net.chrotos.chrotoscloud.paper.games.queue.PaperLeastPlayersQueueManager;
import net.chrotos.chrotoscloud.paper.games.queue.PaperRandomQueueManager;
import net.chrotos.chrotoscloud.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PaperGameManager implements GameManager {
    private final PaperCloud cloud;

    @Override
    public CompletableFuture<GameServer> getGameServer(@NonNull String name) {
        CompletableFuture<GameServer> future = new CompletableFuture<>();

        Registration<Void, GameServerPingResponse> reg = null;
        try {
            reg = cloud.getQueue().register(pingListener(gameServer ->
                    future.complete(gameServer.getGameServer())), "games.server.ping");
            reg.publish(new GameServerPingRequest(name));

            Registration<Void, GameServerPingResponse> finalReg = reg;
            return future.whenComplete((gameServers, throwable) -> finalReg.unsubscribe());
        } catch (IOException e) {
            future.completeExceptionally(e);

            if (reg != null) {
                reg.unsubscribe();
            }
        }

        return future;
    }

    @Override
    public CompletableFuture<List<GameServer>> getGameServers() {
        CompletableFuture<List<GameServer>> future = new CompletableFuture<>();

        Registration<Void, GameServerLookupResponse> reg = null;
        try {
            reg = cloud.getQueue().register(lookupListener(gameServer -> {
                ArrayList<GameServer> gameServers = new ArrayList<>(gameServer.getGameServers());
                future.complete(gameServers);
            }), "games.server.lookup");

            reg.publish(new GameServerLookupRequest());

            Registration<Void, GameServerLookupResponse> finalReg = reg;
            return future.whenComplete((gameServers, throwable) -> finalReg.unsubscribe());
        } catch (IOException e) {
            future.completeExceptionally(e);

            if (reg != null) {
                reg.unsubscribe();
            }
        }

        return future;
    }

    @Override
    public CompletableFuture<List<GameServer>> getGameServers(@NonNull String gameMode) {
        CompletableFuture<List<GameServer>> future = new CompletableFuture<>();

        Registration<Void, GameServerLookupResponse> reg = null;
        try {
            reg = cloud.getQueue().register(lookupListener(gameServer -> {
                ArrayList<GameServer> gameServers = new ArrayList<>(gameServer.getGameServers());
                future.complete(gameServers);
            }), "games.server.lookup");

            reg.publish(new GameServerLookupRequest(gameMode));

            Registration<Void, GameServerLookupResponse> finalReg = reg;
            return future.whenComplete((gameServers, throwable) -> finalReg.unsubscribe());
        } catch (IOException e) {
            future.completeExceptionally(e);

            if (reg != null) {
                reg.unsubscribe();
            }
        }

        return future;
    }


    @Override
    public CompletableFuture<GameServer> getRandom(@NonNull String gameMode) {
        return getGameServers(gameMode).thenApply(gameServers -> {
            List<GameServer> servers = gameServers.stream()
                    .filter(server -> server.getMaxPlayers() == 0 || server.getPlayerCount() < server.getMaxPlayers())
                    .collect(Collectors.toList());

            if (servers.size() > 0) {
                return servers.get((int) (Math.random() * servers.size()));
            }

            return null;
        });
    }

    @Override
    public void requestTeleport(@NonNull GameServer server, @NonNull Player player) {
        cloud.getQueue().publish("player.teleport.server",
                new PlayerTeleportToServerRequest(player.getUniqueId(), server.getName()));
    }

    @Override
    public QueueManager getQueueManager(@NonNull QueueMode queueMode, @NonNull String gameMode) {
        return switch (queueMode) {
            case RANDOM -> new PaperRandomQueueManager(gameMode, this);
            case LEAST_PLAYERS -> new PaperLeastPlayersQueueManager(gameMode, this);
            default -> throw new IllegalArgumentException("Not implemented");
        };
    }

    private Listener<Void, GameServerLookupResponse> lookupListener(Consumer<GameServerLookupResponse> callback) {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<Void> object, @NonNull String sender) {
            }

            @Override
            public void onReply(@NonNull Message<GameServerLookupResponse> object, @NonNull String sender) {
                callback.accept(object.getMessage());
            }

            @Override
            public Class<GameServerLookupResponse> getReplyClass() {
                return GameServerLookupResponse.class;
            }

            @Override
            public @NonNull Class<Void> getMessageClass() {
                return Void.class;
            }
        };
    }

    private Listener<Void, GameServerPingResponse> pingListener(Consumer<GameServerPingResponse> callback) {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<Void> object, @NonNull String sender) {
            }

            @Override
            public void onReply(@NonNull Message<GameServerPingResponse> object, @NonNull String sender) {
                callback.accept(object.getMessage());
            }

            @Override
            public Class<GameServerPingResponse> getReplyClass() {
                return GameServerPingResponse.class;
            }

            @Override
            public @NonNull Class<Void> getMessageClass() {
                return Void.class;
            }
        };
    }
}
