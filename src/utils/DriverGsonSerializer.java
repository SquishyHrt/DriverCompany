package utils;

import com.google.gson.*;
import model.Destination;
import model.Driver;

import java.lang.reflect.Type;

public class DriverGsonSerializer implements JsonSerializer<Driver> {
    @Override
    public JsonElement serialize(Driver driver, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", driver.getLogin());
        jsonObject.addProperty("name", driver.getName());
        jsonObject.addProperty("surname", driver.getSurname());
        jsonObject.addProperty("birthDate", driver.getBirthDate().toString());
        jsonObject.addProperty("phoneNum", driver.getPhoneNum());
        jsonObject.addProperty("medCertificateDate", driver.getMedCertificateDate().toString());
        jsonObject.addProperty("medCertificateNumber", driver.getMedCertificateNumber());
        jsonObject.addProperty("driverLicense", driver.getDriverLicense());


        JsonArray destinationsArray = new JsonArray();
        for (Destination destination : driver.getMyDestinations()) {
            destinationsArray.add(jsonSerializationContext.serialize(destination));
        }
        jsonObject.add("myDestinations", destinationsArray);

        return jsonObject;
    }
}
