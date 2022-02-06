package net.chrotos.chrotoscloud.paper.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.chrotos.chrotoscloud.Cloud;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PaperChatRenderer implements io.papermc.paper.chat.ChatRenderer {
    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        net.chrotos.chrotoscloud.player.Player player = Cloud.getInstance().getPlayerManager()
                                                                            .getPlayer(source.getUniqueId());

        if (player != null) {
            return player.getPrefixes() //TODO configurable chat format
                            .append(Component.text(player.getName()))
                            .append(Component.text(":"))
                            .append(Component.space())
                            .append(message.color(NamedTextColor.DARK_GRAY)); //TODO configurable
        }

        return ChatRenderer.defaultRenderer().render(source, sourceDisplayName, message, viewer);
    }
}
