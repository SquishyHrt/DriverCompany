package utils;

import com.google.gson.*;
import model.Cargo;
import model.Comment;
import model.Forum;

import java.lang.reflect.Type;

public class ForumGsonSerializer implements JsonSerializer<Forum> {
    @Override
    public JsonElement serialize(Forum forum, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", forum.getId());
        jsonObject.addProperty("name", forum.getName());

        JsonArray comments = new JsonArray();
        for (Comment comment: forum.getComments()) {
            comments.add(jsonSerializationContext.serialize(comment));
        }
        jsonObject.add("comments", comments);

        return jsonObject;
    }
}
