package utils;

import com.google.gson.*;
import model.Destination;
import model.Manager;

import java.lang.reflect.Type;

public class ManagerGsonSerializer implements JsonSerializer<Manager> {
    @Override
    public JsonElement serialize(Manager manager, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", manager.getLogin());
        jsonObject.addProperty("name", manager.getName());
        jsonObject.addProperty("surname", manager.getSurname());
        jsonObject.addProperty("birthDate", manager.getBirthDate().toString());
        jsonObject.addProperty("phoneNum", manager.getPhoneNum());
        jsonObject.addProperty("email", manager.getEmail());
        jsonObject.addProperty("employmentDate", manager.getEmploymentDate().toString());
        jsonObject.addProperty("isAdmin", manager.isAdmin());

        JsonArray destinationsArray = new JsonArray();
        for (Destination destination : manager.getDestinationList()) {
            destinationsArray.add(jsonSerializationContext.serialize(destination));
        }
        jsonObject.add("destinationList", destinationsArray);

        return jsonObject;
    }
}
