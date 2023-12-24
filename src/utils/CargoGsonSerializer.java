package utils;

import com.google.gson.*;
import model.Cargo;
import model.Destination;
import model.Driver;

import java.lang.reflect.Type;

public class CargoGsonSerializer implements JsonSerializer<Cargo> {
    @Override
    public JsonElement serialize(Cargo cargo, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", cargo.getId());
        jsonObject.addProperty("title", cargo.getTitle());
        jsonObject.addProperty("dateCreated", cargo.getDateCreated().toString());
        jsonObject.addProperty("dateUpdated", cargo.getDateUpdated() != null ? cargo.getDateUpdated().toString() : "");
        jsonObject.addProperty("weight", cargo.getWeight());
        jsonObject.addProperty("description", cargo.getDescription());
        jsonObject.addProperty("cargoType", cargo.getCargoType().toString());
        jsonObject.addProperty("assignedDestination", cargo.getAssignedDestination() != null ? cargo.getAssignedDestination().getId() : null);

        return jsonObject;
    }
}
