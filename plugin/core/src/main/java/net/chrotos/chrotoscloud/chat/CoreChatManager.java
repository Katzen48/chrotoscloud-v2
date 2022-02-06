package net.chrotos.chrotoscloud.chat;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import net.chrotos.chrotoscloud.player.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CoreChatManager implements ChatManager {
    private final List<ChatPrefix> prefixes = new ArrayList<>();
    private final LoadingCache<Player, Component> prefixCache = CacheBuilder.newBuilder()
                                                                .expireAfterWrite(Duration.ofSeconds(60))
                                                                .build(CacheLoader.from(this::getFormattedPrefix));

    @Override
    @NonNull
    public Component getPrefixes(@NonNull Player player) {
        try {
            return prefixCache.get(player);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Component.empty();
        }
    }

    @Override
    public void registerPrefix(@NonNull ChatPrefix prefix) {
        if (!prefixes.contains(prefix)) {
            prefixes.add(prefix);
        }
    }

    private Component getFormattedPrefix(@NonNull Player player) {
        Component prefixBuilder = Component.empty();

        int i = 0;
        for (ChatPrefix prefix : getApplicablePrefixes(player)) {
            if (i > 0) {
                prefixBuilder = prefixBuilder.append(Component.space());
            }
            prefixBuilder = prefixBuilder.append(Component.text(prefix.getPrefix(player), prefix.getColor()));

            i++;
        }

        if (i > 0) {
            prefixBuilder = prefixBuilder.append(Component.space());
        }

        return prefixBuilder.append(getRankColored(player));
    }

    private List<ChatPrefix> getApplicablePrefixes(@NonNull Player player) {
        List<ChatPrefix> applicablePrefixes = prefixes.stream().filter(prefix -> prefix.isActive(player))
                                                            .collect(Collectors.toList());

        ListIterator<ChatPrefix> it = applicablePrefixes.listIterator();
        while(it.hasNext()) {
            ChatPrefix prefixToAdd = it.next();

            if (applicablePrefixes.stream().anyMatch(prefix ->  prefix.getSlot() == prefixToAdd.getSlot() &&
                                                                prefix.getPriority() > prefixToAdd.getPriority())) {
                it.remove();
            }
        }

        applicablePrefixes.sort(Comparator.comparingInt(ChatPrefix::getSlot));

        return applicablePrefixes;
    }

    @Override
    @NonNull
    public Component getRankColored(@NonNull Player player) {
        return player.getRank() != null ?
                LegacyComponentSerializer.builder().build().deserialize(player.getRank().getPrefix()) :
                Component.empty();
    }
}
