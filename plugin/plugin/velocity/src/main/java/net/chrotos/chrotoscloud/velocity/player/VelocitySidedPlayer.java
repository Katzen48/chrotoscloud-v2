package net.chrotos.chrotoscloud.velocity.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.chrotos.chrotoscloud.velocity.VelocityCloud;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class VelocitySidedPlayer implements SidedPlayer {
    private final Player sidedObject;

    @Override
    public UUID getUniqueId() {
        return getSidedObject().getUniqueId();
    }

    @Override
    public String getName() {
        return getSidedObject().getUsername();
    }

    @Override
    public Locale getLocale() {
        return getSidedObject().getEffectiveLocale();
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash) {
        setResourcePack(url, hash, false);
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required) {
        setResourcePack(url, hash, required, null);
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, TextComponent prompt) {
        ResourcePackInfo.Builder builder = getResourcePackBuilder(url);
        builder.setHash(hash.getBytes(StandardCharsets.UTF_8));
        builder.setShouldForce(required);
        builder.setPrompt(prompt);

        sidedObject.sendResourcePackOffer(builder.build());
    }

    @Override
    public InetAddress getIPAddress() {
        return sidedObject.getRemoteAddress().getAddress();
    }

    private ResourcePackInfo.Builder getResourcePackBuilder(@NonNull String url) {
        return ((VelocityCloud)Cloud.getInstance()).getProxyServer().createResourcePackBuilder(url);
    }

    @Override
    public void kick(Component message) {
        sidedObject.disconnect(message == null ? Component.empty() : message);
    }
}
