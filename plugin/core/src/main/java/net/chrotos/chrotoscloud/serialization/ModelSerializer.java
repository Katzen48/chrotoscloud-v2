package net.chrotos.chrotoscloud.serialization;

import com.google.gson.*;
import lombok.NonNull;
import lombok.Setter;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.Model;
import net.chrotos.chrotoscloud.persistence.DataSelectFilter;

import java.lang.reflect.Type;

public class ModelSerializer implements JsonSerializer<Model>, JsonDeserializer<Model> {
    @Setter
    @NonNull
    private Gson gson;

    @Override
    public Model deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        try {
            Class<?> clazz = Class.forName(jsonObject.get("class").getAsString());
            Object primaryKey = gson.fromJson(jsonObject.get("primaryKey"), Cloud.getInstance().getPersistence()
                                    .getPrimaryKeyType(clazz));

            return (Model) Cloud.getInstance().getPersistence().getOne(clazz,
                    DataSelectFilter.builder().primaryKeyValue(primaryKey).build());
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Model src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("class", src.getClass().getName());

        jsonObject.add("primaryKey", gson.toJsonTree(Cloud.getInstance().getPersistence().getPrimaryKey(src)));

        return jsonObject;
    }
}
