package utils;

import com.google.gson.*;
import model.Comment;

import java.lang.reflect.Type;

public class CommentGsonSerializer implements JsonSerializer<Comment> {
    @Override
    public JsonElement serialize(Comment comment, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", comment.getId());
        jsonObject.addProperty("text", comment.getText());
        jsonObject.addProperty("username", comment.getUsername());
        jsonObject.addProperty("parent", comment.getParent() != null ? comment.getParent().getId() : null);
        jsonObject.addProperty("forum", comment.getForum() != null ? comment.getForum().getId() : null);

        JsonArray repliesArray = new JsonArray();
        for (Comment reply : comment.getReplies()) {
            repliesArray.add(jsonSerializationContext.serialize(reply));
        }
        jsonObject.add("replies", repliesArray);

        return jsonObject;
    }
}
