package net.chrotos.chrotoscloud.paper.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.chrotos.chrotoscloud.Cloud;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaperChatRenderer implements io.papermc.paper.chat.ChatRenderer {
    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        net.chrotos.chrotoscloud.player.Player player = Cloud.getInstance().getPlayerManager()
                                                                            .getPlayer(source.getUniqueId());

        if (player != null) {
            return  LegacyComponentSerializer.builder().build().
                    deserialize(String.format(
                    "%s §r%s:§8",                     //TODO configurable chat format
                    player.getPrefixes(),
                    player.getName()
            )).append(Component.space())
                    .append(message);
        }

        return ChatRenderer.defaultRenderer().render(source, sourceDisplayName, message, viewer);
    }
}
