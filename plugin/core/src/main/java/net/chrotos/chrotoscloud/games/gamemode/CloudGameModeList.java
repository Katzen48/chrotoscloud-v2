package net.chrotos.chrotoscloud.games.gamemode;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.common.KubernetesListObject;
import io.kubernetes.client.openapi.models.V1ListMeta;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CloudGameModeList implements KubernetesListObject {
    @SerializedName("apiVersion")
    private String apiVersion;
    @SerializedName("kind")
    private String kind;
    @SerializedName("metadata")
    private V1ListMeta metadata = null;
    @SerializedName("items")
    private List<CloudGameMode> items = new ArrayList<>();
}
