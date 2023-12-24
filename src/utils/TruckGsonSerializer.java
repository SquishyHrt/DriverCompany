package utils;

import com.google.gson.*;
import model.Cargo;
import model.Truck;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TruckGsonSerializer implements JsonSerializer<Truck> {
    @Override
    public JsonElement serialize(Truck truck, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", truck.getId());
        jsonObject.addProperty("make", truck.getMake());
        jsonObject.addProperty("model", truck.getModel());
        jsonObject.addProperty("year", truck.getYear());
        jsonObject.addProperty("odometer", truck.getOdometer());
        jsonObject.addProperty("fuelTankCapacity", truck.getFuelTankCapacity());
        jsonObject.addProperty("tyreType", truck.getTyreType().toString());
        jsonObject.addProperty("currentDestination", truck.getCurrentDestination() != null ? truck.getCurrentDestination().toString() : null);

        return jsonObject;
    }
}
