package net.chrotos.chrotoscloud.games.gamemode;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import io.kubernetes.client.util.generic.KubernetesApiResponse;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class CloudGameModeManager implements GameModeManager {
    private final GenericKubernetesApi<CloudGameMode, CloudGameModeList> api;

    @SneakyThrows
    public CloudGameModeManager() {
        ApiClient apiClient = Config.defaultClient();
        Configuration.setDefaultApiClient(apiClient);

        this.api = new GenericKubernetesApi<>(CloudGameMode.class, CloudGameModeList.class,
                "chrotoscloud.chrotos.net", "apiextensions.k8s.io/v1", "servers", apiClient);
    }

    @Override
    public List<? extends GameMode> getGameModes() {
        KubernetesApiResponse<CloudGameModeList> response = api.list("servers"); // TODO switch to env variable

        if (response.isSuccess()) {
            return response.getObject().getItems();
        }

        return new ArrayList<>();
    }

    @Override
    public GameMode getByName(@NonNull String name) {
        KubernetesApiResponse<CloudGameMode> response = api.get("servers", name); // TODO switch to env variable

        if (response.isSuccess()) {
            return response.getObject();
        }

        return null;
    }
}
