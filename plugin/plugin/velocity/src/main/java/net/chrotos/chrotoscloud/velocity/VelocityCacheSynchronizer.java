package net.chrotos.chrotoscloud.velocity;

import com.google.common.collect.Sets;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.messaging.pubsub.Registration;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class VelocityCacheSynchronizer {
    private final Set<String> proxies = Sets.newConcurrentHashSet();
    private final CloudPlugin plugin;
    private Registration pubSub;
    private ScheduledTask checkTask;
    private String proxyKey;

    public void initialize() {
        proxyKey = "proxy:" + System.getenv("HOSTNAME") + ":players";

        pubSub = plugin.cloud.getPubSub().register(this::onPubSub, "proxy:register");
        checkTask = plugin.proxyServer.getScheduler()
                                        .buildTask(plugin, this::checkProxies)
                                        .repeat(5L, TimeUnit.SECONDS).schedule();
        plugin.cloud.getPubSub().publish("proxy:register", System.getenv("HOSTNAME"));
    }

    public void destruct() {
        pubSub.unsubscribe("proxy:register");

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

    private void onPubSub(String channel, String message) {
        if (channel.equalsIgnoreCase("proxy:register")) {
            if (!message.equals(System.getenv("HOSTNAME"))) {
                proxies.add(message);
            }
        }
    }

    private void checkProxies() {
        proxies.removeIf(proxy -> !plugin.cloud.getCache().exists("proxy:" + proxy + ":players"));
        plugin.cloud.getCache().expire(proxyKey, Duration.ofSeconds(10L));
    }
}
