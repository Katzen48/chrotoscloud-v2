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
                "chrotoscloud.chrotos.net", "v1", "gamemodes", apiClient);
    }

    @Override
    public List<? extends GameMode> getGameModes() {
        String namespaces = System.getenv("CHROTOSCLOUD_NAMESPACES");
        if (namespaces == null || namespaces.isBlank()) {
            namespaces = "servers";
        }
        List<CloudGameMode> gameModes = new ArrayList<>();

        for (String namespace : namespaces.split(",")) {
            KubernetesApiResponse<CloudGameModeList> response = api.list(namespace);

            if (response.isSuccess()) {
                gameModes.addAll(response.getObject().getItems());
            }
        }

        return gameModes;
    }

    @Override
    public GameMode getByName(@NonNull String name) {
        String namespaces = System.getenv("CHROTOSCLOUD_NAMESPACES");
        if (namespaces == null || namespaces.isBlank()) {
            namespaces = "servers";
        }

        for (String namespace : namespaces.split(",")) {
            KubernetesApiResponse<CloudGameMode> response = api.get(namespace, name);

            if (response.isSuccess()) {
                return response.getObject();
            }
        }

        return null;
    }
}
