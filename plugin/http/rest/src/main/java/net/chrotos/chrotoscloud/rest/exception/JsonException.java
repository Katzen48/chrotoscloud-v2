package net.chrotos.chrotoscloud.rest.exception;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public abstract class JsonException extends WebApplicationException {
    private static final Gson GSON = new Gson();

    public JsonException(String errorMessage, int code) {
        super(getResponse(errorMessage, code));
    }

    private static Response getResponse(String errorMessage, int code) {
        JsonObject object = new JsonObject();
        JsonObject error = new JsonObject();
        error.addProperty("code", code);
        error.addProperty("message", errorMessage);

        object.add("error", error);

        return Response.status(code).type(MediaType.APPLICATION_JSON).entity(GSON.toJson(object)).build();
    }
}
