package utils;

import com.google.gson.*;
import model.Cargo;
import model.Checkpoint;
import model.Destination;

import java.lang.reflect.Type;

public class DestinationGsonSerializer implements JsonSerializer<Destination> {
    @Override
    public JsonElement serialize(Destination destination, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", destination.getId());
        jsonObject.addProperty("startCity", destination.getStartCity());
        jsonObject.addProperty("endCity", destination.getEndCity());
        jsonObject.addProperty("dateCreated", destination.getDateCreated().toString());
        jsonObject.addProperty("dateUpdated", destination.getDateUpdated() == null ? "" : destination.getDateUpdated().toString());
        jsonObject.addProperty("departureDate", destination.getDepartureDate().toString());
        jsonObject.addProperty("arrivalDate", destination.getArrivalDate().toString());
        jsonObject.addProperty("truck", destination.getTruck().getId());
        jsonObject.addProperty("driver", destination.getDriver().getId());
        jsonObject.addProperty("responsibleManager", destination.getResponsibleManager().getId());

        JsonArray cargoArray = new JsonArray();
        for (Cargo cargo : destination.getCargoList()) {
            cargoArray.add(jsonSerializationContext.serialize(cargo));
        }
        jsonObject.add("cargoList", cargoArray);

        JsonArray checkpointArray = new JsonArray();
        for (Checkpoint checkpoint : destination.getCheckpointList()) {
            checkpointArray.add(jsonSerializationContext.serialize(checkpoint));
        }
        jsonObject.add("checkpointList", checkpointArray);

        return jsonObject;
    }
}
