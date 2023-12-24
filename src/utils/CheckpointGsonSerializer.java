package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.Cargo;
import model.Checkpoint;

import java.lang.reflect.Type;

public class CheckpointGsonSerializer implements JsonSerializer<Checkpoint> {
    @Override
    public JsonElement serialize(Checkpoint checkpoint, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", checkpoint.getId());
        jsonObject.addProperty("address", checkpoint.getAddress());
        jsonObject.addProperty("longStop", checkpoint.isLongStop());
        jsonObject.addProperty("dataArrived", checkpoint.getDateArrived().toString());
        jsonObject.addProperty("assignedDestination", checkpoint.getAssignedDestination() == null ? null : checkpoint.getAssignedDestination().getId());

        return jsonObject;
    }
}
