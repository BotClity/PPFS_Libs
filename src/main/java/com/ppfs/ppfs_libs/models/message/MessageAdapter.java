package com.ppfs.ppfs_libs.models.message;

import com.google.gson.*;
import com.ppfs.ppfs_libs.PPFS_Libs;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement messageElement = jsonObject.get("message");

            if (messageElement.isJsonArray()) {
                JsonArray array = messageElement.getAsJsonArray();
                List<String> messages = new ArrayList<>();
                for (JsonElement element : array) {
                    messages.add(element.getAsString().replace('&', '§'));
                }
                return new Message(messages);
            } else if (messageElement.isJsonPrimitive()) {
                return new Message(messageElement.getAsString().replace('&', '§'));
            }
            throw new JsonParseException("Некорректный формат ключа 'message'.");
        } catch (Exception e) {
            PPFS_Libs.getLogger().error("Ошибка десериализации JSON: " + e.getMessage());
            throw new JsonParseException("Ошибка десериализации сообщения.", e);
        }
    }


    @Override
    public JsonObject serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            JsonObject object = new JsonObject();
            JsonArray jsonArray = new JsonArray();

            for (String msg : src.getRawMessages()) {
                jsonArray.add(msg);
            }

            object.add("message", jsonArray);
            return object;
        } catch (Exception e) {
            PPFS_Libs.getLogger().error("Ошибка сериализации объекта Message: " + e.getMessage());
            PPFS_Libs.getLogger().error("Исходный объект: " + src.toString());
            throw new JsonParseException("Ошибка сериализации сообщения.", e);
        }
    }
}
