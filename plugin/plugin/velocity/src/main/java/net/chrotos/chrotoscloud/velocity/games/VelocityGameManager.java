package net.chrotos.chrotoscloud.velocity.games;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import lombok.Getter;
import lombok.NonNull;
import net.chrotos.chrotoscloud.games.*;
import net.chrotos.chrotoscloud.games.events.*;
import net.chrotos.chrotoscloud.games.gamemode.CloudGameModeManager;
import net.chrotos.chrotoscloud.messaging.queue.Listener;
import net.chrotos.chrotoscloud.messaging.queue.Message;
import net.chrotos.chrotoscloud.messaging.queue.Registration;
import net.chrotos.chrotoscloud.velocity.VelocityCloud;
import net.chrotos.chrotoscloud.velocity.player.VelocitySidedPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VelocityGameManager implements GameManager, AutoCloseable {
    private final VelocityCloud cloud;
    @Getter
    private final CloudGameModeManager gameModeManager = new CloudGameModeManager();
    private final CoreV1Api coreV1Api;
    private Registration<GameServerLookupRequest, Void> lookup;
    private Registration<GameServerPingRequest, Void> ping;
    private Registration<PlayerTeleportToServerRequest, Void> teleport;

    public VelocityGameManager(VelocityCloud cloud) {
        this.cloud = cloud;

        try {
            ApiClient k8sClient = Config.defaultClient();
            Configuration.setDefaultApiClient(k8sClient);
            this.coreV1Api = new CoreV1Api();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<CloudGameServer> getGameServer(@NonNull String name) {
        return getGameServer(name, null);
    }

    private CompletableFuture<CloudGameServer> getGameServer(@NonNull String name, String gameMode) {
        RegisteredServer server = cloud.getProxyServer().getServer(name).orElse(null);

        CompletableFuture<CloudGameServer> future = new CompletableFuture<>();
        if (server == null) {
            future.complete(null);

            return future;
        } else {
            return server.ping().thenApply(ping -> {
                int maxPlayers = 0;
                int playerCount = 0;

                if (ping.getPlayers().isPresent()) {
                    maxPlayers = ping.getPlayers().get().getMax();
                    playerCount = ping.getPlayers().get().getOnline();
                }

                String podGameMode = gameMode;
                if (podGameMode == null) {
                    podGameMode = getPodGameMode(name);
                }

                return new CloudGameServer(name, maxPlayers, playerCount, podGameMode);
            });
        }
    }

    @Override
    public CompletableFuture<List<? extends GameServer>> getGameServers() {
        cloud.getLogger().info("Looking up all game servers");
        return getGameServersByGameMode(null);
    }

    @Override
    public CompletableFuture<List<? extends GameServer>> getGameServers(@NonNull String gameMode) {
        cloud.getLogger().info("Looking up game servers for gamemode {}", gameMode);
        return getGameServersByGameMode(gameMode);
    }

    public CompletableFuture<List<? extends GameServer>> getGameServersByGameMode(String gameMode) {
        return CompletableFuture.supplyAsync(() -> {
                Collection<RegisteredServer> servers;
            if (gameMode == null) {
                servers = cloud.getProxyServer().getAllServers();
            } else {
                List<String> serverNames = getPodsByGameMode(gameMode);
                servers = serverNames.stream().map(serverName -> cloud.getProxyServer().getServer(serverName).orElse(null))
                                    .filter(Objects::nonNull).collect(Collectors.toList());
            }

            CompletableFuture[] gameServers = new CompletableFuture[servers.size()];

            AtomicInteger i = new AtomicInteger();
            servers.forEach(server -> {
                String name = server.getServerInfo().getName();
                gameServers[i.getAndIncrement()] = getGameServer(name, gameMode);
            });

            ArrayList<GameServer> list = new ArrayList<>();
            for (CompletableFuture<GameServer> gameServer : gameServers) {
                GameServer server = gameServer.join();

                if (server != null) {
                    list.add(server);
                }
            }

            return list;
        });
    }

    @Override
    public CompletableFuture<GameServer> getRandom(@NonNull String gameMode) {
        return getGameServers(gameMode).thenApply(gameServers -> {
            if (gameServers.size() > 0) {
                return gameServers.get((int) (Math.random() * gameServers.size()));
            }

            return null;
        });
    }

    @Override
    public void requestTeleport(@NonNull GameServer server, @NonNull net.chrotos.chrotoscloud.player.Player player) {
        cloud.getLogger().info("Trying to teleport {} to {}", player.getName(), server.getName());
        VelocitySidedPlayer sidedPlayer = (VelocitySidedPlayer) player.getSidedPlayer();
        sidedPlayer.getSidedObject().createConnectionRequest(cloud.getProxyServer().getServer(server.getName()).get())
                    .fireAndForget();
    }

    @Override
    public QueueManager getQueueManager(@NonNull QueueMode queueMode, @NonNull String gameMode) {
        throw new IllegalStateException("Queueing is currently only accessible from the Paper side.");
    }

    public void initialize() throws IOException {
        lookup = cloud.getQueue().register(onLookup(), "games.server.lookup");
        ping = cloud.getQueue().register(onPing(), "games.server.ping");
        teleport = cloud.getQueue().register(onTeleport(), "player.teleport.server");
    }

    protected List<String> getPodsByGameMode(@NonNull String gameMode) {
        try {
            ArrayList<String> pods = new ArrayList<>();
            V1PodList list = coreV1Api.listNamespacedPod("servers", null, false, null,
                    null, "net.chrotos.chrotoscloud.gameserver/gamemode=" + gameMode, null, null,
                    null, null);

            for (V1Pod pod : list.getItems()) {
                pods.add(pod.getMetadata().getName());
            }

            return pods;
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getPodGameMode(@NonNull String podName) {
        try {
            V1PodList list = coreV1Api.listNamespacedPod("servers", null, false, null,
                    "metadata.name=" + podName, null, null, null,
                    null, null);

            if (list.getItems().isEmpty()) {
                return null;
            }

            return list.getItems().get(0).getMetadata().getLabels().get("net.chrotos.chrotoscloud.gameserver/gamemode");
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Listener<GameServerLookupRequest, Void> onLookup() {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<GameServerLookupRequest> object, @NonNull String sender) {
                cloud.getLogger().info("Got GameServerLookupRequest");
                String gameMode = object.getMessage().getGameMode();
                CompletableFuture<List<? extends GameServer>> gameServers;

                if (gameMode == null) {
                    gameServers = getGameServers();
                } else {
                    gameServers = getGameServers(gameMode);
                }

                gameServers.thenAccept(servers -> {
                    ArrayList<CloudGameServer> list = new ArrayList<>();
                    servers.forEach(server -> list.add((CloudGameServer) server));

                    try {
                        cloud.getLogger().info("Replying to GameServerLookupRequest");
                        object.replyTo(new GameServerLookupResponse(list));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onReply(@NonNull Message<Void> object, @NonNull String sender) {}

            @Override
            public Class<Void> getReplyClass() {
                return null;
            }

            @Override
            public @NonNull Class<GameServerLookupRequest> getMessageClass() {
                return GameServerLookupRequest.class;
            }
        };
    }

    private Listener<GameServerPingRequest, Void> onPing() {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<GameServerPingRequest> object, @NonNull String sender) {
                cloud.getLogger().info("Got GameServerPingRequest");
                String name = object.getMessage().getName();

                getGameServer(name).thenAccept(server -> {
                    try {
                        cloud.getLogger().info("Replying to GameServerPingRequest");
                        object.replyTo(new GameServerPingResponse(server));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onReply(@NonNull Message<Void> object, @NonNull String sender) {}

            @Override
            public Class<Void> getReplyClass() {
                return null;
            }

            @Override
            public @NonNull Class<GameServerPingRequest> getMessageClass() {
                return GameServerPingRequest.class;
            }
        };
    }

    private Listener<PlayerTeleportToServerRequest, Void> onTeleport() {
        return new Listener<>() {
            @Override
            public void onMessage(@NonNull Message<PlayerTeleportToServerRequest> object, @NonNull String sender) {
                cloud.getLogger().info("Got PlayerTeleportToServerRequest");
                Player player = cloud.getProxyServer().getPlayer(object.getMessage().getPlayerId()).orElse(null);
                RegisteredServer server = cloud.getProxyServer().getServer(object.getMessage().getServerName()).orElse(null);

                if (player == null || server == null) {
                    return;
                }
                cloud.getLogger().info("Connecting player to server {}", server.getServerInfo().getName());
                player.createConnectionRequest(server).fireAndForget();
            }

            @Override
            public void onReply(@NonNull Message<Void> object, @NonNull String sender) {

            }

            @Override
            public Class<Void> getReplyClass() {
                return null;
            }

            @Override
            public @NonNull Class<PlayerTeleportToServerRequest> getMessageClass() {
                return PlayerTeleportToServerRequest.class;
            }
        };
    }

    @Override
    public void close() throws Exception {
        if (lookup != null) {
            lookup.close();
        }
        if (ping != null) {
            ping.close();
        }
        if (teleport != null) {
            teleport.close();
        }
    }
}
