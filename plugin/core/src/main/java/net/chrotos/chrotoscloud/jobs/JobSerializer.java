package net.chrotos.chrotoscloud.jobs;

import com.google.gson.*;
import net.chrotos.chrotoscloud.serialization.JsonUtils;

import java.lang.reflect.Type;

public class JobSerializer implements JsonSerializer<AbstractJob>, JsonDeserializer<AbstractJob> {
    @Override
    public AbstractJob deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        try {
            Class<? extends AbstractJob> clazz = (Class<? extends AbstractJob>) Class.forName(jsonObject.get("class").getAsString());
            jsonObject.remove("class");

            return JsonUtils.fromJson(jsonObject, clazz);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(AbstractJob src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = JsonUtils.toJsonTree(src).getAsJsonObject();
        jsonObject.addProperty("class", src.getClass().getName());

        return jsonObject;
    }
}
