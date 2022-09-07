package net.chrotos.chrotoscloud.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.chrotos.chrotoscloud.Model;

import java.io.Reader;

public class JsonUtils {
    private static final Gson gson;

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static JsonElement toJsonTree(Object object) {
        return gson.toJsonTree(object);
    }

    public static <E> E fromJson(String json, Class<E> type) {
        return gson.fromJson(json, type);
    }

    public static <E> E fromJson(JsonElement json, Class<E> type) {
        return gson.fromJson(json, type);
    }

    public static <E> E fromJson(Reader reader, Class<E> type) {
        return gson.fromJson(reader, type);
    }

    static {
        ModelSerializer modelSerializer = new ModelSerializer();
        gson = new GsonBuilder()
                .registerTypeAdapter(Model.class, modelSerializer)
                .create();
        modelSerializer.setGson(gson);
    }
}
