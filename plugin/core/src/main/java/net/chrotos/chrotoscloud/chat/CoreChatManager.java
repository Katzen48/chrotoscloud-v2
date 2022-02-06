package net.chrotos.chrotoscloud.chat;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.chrotos.chrotoscloud.player.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CoreChatManager implements ChatManager {
    private final List<ChatPrefix> prefixes = new ArrayList<>();
    private final LoadingCache<Player, String> prefixCache = CacheBuilder.newBuilder()
                                                                .expireAfterWrite(Duration.ofSeconds(60))
                                                                .build(CacheLoader.from(this::getFormattedPrefix));

    @Override
    public String getPrefixes(Player player) { //TODO Change to kyori component
        try {
            return prefixCache.get(player);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void registerPrefix(ChatPrefix prefix) {
        if (!prefixes.contains(prefix)) {
            prefixes.add(prefix);
        }
    }

    private String getFormattedPrefix(Player player) {
        StringBuilder prefixBuilder = new StringBuilder();

        for (ChatPrefix prefix : getApplicablePrefixes(player)) {
            prefixBuilder.append(prefix.getColor())
                            .append(prefix.getPrefix(player))
                            .append("&r ");
        }

        return String.format("%s %s", prefixBuilder.toString().trim(), getRankColor(player));
    }

    private List<ChatPrefix> getApplicablePrefixes(Player player) {
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
    public String getRankColor(Player player) { //TODO Change to kyori component
        return player.getRank() != null ? player.getRank().getPrefix() : "";
    }
}
