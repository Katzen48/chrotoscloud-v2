package net.chrotos.chrotoscloud.velocity;

import com.google.common.collect.Sets;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VelocityCacheSynchronizer {
    private final Set<String> proxies = Sets.newConcurrentHashSet();
    private final CloudPlugin plugin;
    private ScheduledTask checkTask;
    private String proxyKey;
    private int playerCount;

    public void initialize() {
        proxyKey = "proxy:" + Cloud.getInstance().getHostname() + ":players";

        checkTask = plugin.proxyServer.getScheduler()
                                        .buildTask(plugin, this::checkProxies)
                                        .repeat(5L, TimeUnit.SECONDS).schedule();
    }

    public void destruct() {
        if (checkTask.status() == TaskStatus.SCHEDULED) {
            checkTask.cancel();
        }
    }

    @Subscribe(order = PostOrder.FIRST, async = false)
    public void onLogin(LoginEvent event) {
        if (!event.getResult().isAllowed() || !plugin.proxyServer.getConfiguration().isOnlineMode()) {
            return;
        }

        try {
            for (String proxy : proxies) {
                if (plugin.cloud.getCache().setContains("proxy:" + proxy + ":players",
                        event.getPlayer().getUniqueId().toString())) {
                    event.setResult(ResultedEvent.ComponentResult.denied(Component.text("You are already connected")));

                    return;
                }
            }
        } catch (Exception e) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Error")));
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPostLogin(PostLoginEvent event) {
        plugin.cloud.getCache().setAdd(proxyKey, event.getPlayer().getUniqueId().toString());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onDisconnect(DisconnectEvent event) {
        String value = event.getPlayer().getUniqueId().toString();

        if (!plugin.cloud.getCache().setContains(proxyKey, value)) {
            return;
        }

        plugin.cloud.getCache().setRemove(proxyKey, value);
    }

    @Subscribe(order = PostOrder.LAST)
    public void onProxyPing(ProxyPingEvent event) {
        int onlinePlayers = getPlayerCount();

        event.setPing(event.getPing().asBuilder()
                        .onlinePlayers(onlinePlayers)
                        .maximumPlayers(onlinePlayers + 1)
                        .clearMods()
                        .clearSamplePlayers()
                        .build());
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public Set<String> getPlayers() {
        Set<String> players = new HashSet<>();

        for (String proxy : proxies) {
            players.addAll(plugin.cloud.getCache().setMembers("proxy:" + proxy + ":players"));
        }

        return players;
    }

    private void checkProxies() {
        Set<String> keys = plugin.cloud.getCache().keys("proxy:*:players");

        proxies.clear();
        keys.forEach((key) -> proxies.add(key.split(":")[1]));

        plugin.cloud.getCache().expire(proxyKey, Duration.ofSeconds(10L));

        playerCount = (int) proxies.stream().mapToLong(
                (proxy) -> plugin.cloud.getCache().setSize("proxy:" + proxy + ":players")).sum();
    }
}
