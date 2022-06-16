package net.chrotos.chrotoscloud.velocity.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.player.SidedPlayer;
import net.chrotos.chrotoscloud.velocity.VelocityCloud;
import net.kyori.adventure.text.Component;

import java.nio.charset.StandardCharsets;
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
    public String getResourcePackHash() {
        return sidedObject.getAppliedResourcePack() != null ?
                new String(sidedObject.getAppliedResourcePack().getHash(), StandardCharsets.UTF_8) : null;
    }

    @Override
    public void setResourcePack(@NonNull String url) {
        ResourcePackInfo.Builder builder = getResourcePackBuilder(url);
        sidedObject.sendResourcePackOffer(builder.build());
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash) {
        ResourcePackInfo.Builder builder = getResourcePackBuilder(url);
        builder.setHash(hash.getBytes(StandardCharsets.UTF_8));

        sidedObject.sendResourcePackOffer(builder.build());
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required) {
        ResourcePackInfo.Builder builder = getResourcePackBuilder(url);
        builder.setHash(hash.getBytes(StandardCharsets.UTF_8));
        builder.setShouldForce(required);

        sidedObject.sendResourcePackOffer(builder.build());
    }

    @Override
    public void setResourcePack(@NonNull String url, @NonNull String hash, boolean required, @NonNull Component prompt) {
        ResourcePackInfo.Builder builder = getResourcePackBuilder(url);
        builder.setHash(hash.getBytes(StandardCharsets.UTF_8));
        builder.setShouldForce(required);
        builder.setPrompt(prompt);

        sidedObject.sendResourcePackOffer(builder.build());
    }

    private ResourcePackInfo.Builder getResourcePackBuilder(@NonNull String url) {
        return ((VelocityCloud)Cloud.getInstance()).getProxyServer().createResourcePackBuilder(url);
    }
}
