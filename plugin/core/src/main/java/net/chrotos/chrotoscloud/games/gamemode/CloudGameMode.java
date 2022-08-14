package net.chrotos.chrotoscloud.games.gamemode;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.Getter;

import java.util.List;

@Getter
public class CloudGameMode implements GameMode, KubernetesObject {
    @SerializedName("apiVersion")
    private String apiVersion;
    @SerializedName("kind")
    private String kind;
    @SerializedName("metadata")
    private V1ObjectMeta metadata = null;
    @SerializedName("spec")
    private CloudGameModeSpec spec;

    @Override
    public String getVersion() {
        return spec.getVersion();
    }

    @Override
    public String getCloudVersion() {
        return spec.getCloudVersion();
    }

    @Override
    public GameModeMaps getMaps() {
        return spec.getMaps();
    }

    @Override
    public List<GameModePlugin> getPlugins() {
        return spec.getPlugins();
    }

    @Override
    public GameModeResourcePack getResourcePack() {
        return spec.getResourcePack();
    }
}
